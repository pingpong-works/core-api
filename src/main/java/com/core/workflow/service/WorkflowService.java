package com.core.workflow.service;

import com.core.approval.entity.Approval;
import com.core.approval.repository.ApprovalRepository;
import com.core.client.auth.AuthServiceClient;

import com.core.client.auth.UserResponse;
import com.core.document.entity.Document;
import com.core.document.service.DocumentService;
import com.core.exception.BusinessLogicException;
import com.core.exception.ExceptionCode;
import com.alarm.kafka.ApprovalProducer;
import com.core.workflow.entity.Workflow;
import com.core.workflow.repository.WorkflowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;


@Service
@Transactional
public class WorkflowService {
    private final WorkflowRepository workflowRepository;
    private final ApprovalRepository approvalRepository;
    private final DocumentService documentService;
    private final AuthServiceClient authServiceClient;
    private final ApprovalProducer approvalProducer;


    public WorkflowService(WorkflowRepository workflowRepository, ApprovalRepository approvalRepository, DocumentService documentService, AuthServiceClient authServiceClient, ApprovalProducer approvalProducer, ApprovalProducer approvalProducer1) {
        this.workflowRepository = workflowRepository;
        this.approvalRepository = approvalRepository;
        this.documentService = documentService;
        this.authServiceClient = authServiceClient;
        this.approvalProducer = approvalProducer;
    }

    public Workflow createWorkflow(Workflow workflow) {
        //approval : 한 workflow 내에 employeeId 중복 안되게하기
        Set<Long> employeeIds = new HashSet<>();

        workflow.getApprovals().forEach(approval -> {

            //존재하는 직원인지 확인
            verifiedMember(approval.getEmployeeId());

            if (!employeeIds.add(approval.getEmployeeId())) {
                System.out.println("승인자 중복 : " + approval.getEmployeeId());
                throw new BusinessLogicException(ExceptionCode.EMPLOYEE_IN_APPROVAL_IS_DUPLICATE);
            }
        });

        return workflowRepository.save(workflow);
    }

    //workflow 자체는 수정되지 않고 승인 과정만 수정 가능.
    public Workflow updateWorkflow(Approval approval) {

        Approval findApproval = findVerifiedApproval(approval.getId());

        //반려상태 일 경우에는 결재 할 수 없다.
        if(findApproval.getApprovalStatus().equals(Approval.ApprovalStatus.REJECT)) {
            throw new BusinessLogicException(ExceptionCode.DOCUMENT_HAS_BEEN_REJECTED);
        }

        // 전결일 경우를 제외하고 결재권자와 결재순서가 일치하는 지 확인
        if(!approval.getApprovalStatus().equals(Approval.ApprovalStatus.FINALIZE)) {
            if ((!findApproval.getEmployeeId().equals(approval.getEmployeeId())) ||
                    findApproval.getApprovalOrder() != approval.getApprovalOrder()) {

                throw new BusinessLogicException(ExceptionCode.APPROVAL_PERMISSION_DENIED);
            }
        }
        //현재 결재라인 3번째인데 승인자 1번이 결재를 할 수 없다
        if(findApproval.getWorkflow().getCurrentStep() >  approval.getApprovalOrder()) {
            throw new BusinessLogicException(ExceptionCode.APPROVAL_PERMISSION_DENIED);
        }
        //기존 workflow가 전부 승인 상태일 경우 수정할 수 없어야 한다. == approval size 로 확인
        if(findApproval.getWorkflow().getCurrentStep() == findApproval.getWorkflow().getApprovals().size()) {
            throw new BusinessLogicException(ExceptionCode.DOCUMENT_HAS_BEEN_FINALIZED);
        }

        //전결일 경우에 결재순서 마지막으로 수정되어야 한다.
        if(findApproval.getApprovalStatus().equals(Approval.ApprovalStatus.FINALIZE)) {
            findApproval.setApprovalOrder(approval.getWorkflow().getApprovals().size());
        }

        //Approval 저장
        findApproval.setApprovalStatus(approval.getApprovalStatus());
        findApproval.setMessage(approval.getMessage());
        approvalRepository.save(findApproval);

        //workflow 단계 수정 및 저장
        Workflow workflow = findApproval.getWorkflow();
        updateWorkflowStep(workflow, findApproval.getApprovalStatus());

        // document 상태 저장
        updateDocumentStatus(workflow);

        return workflow;
    }

    // workflow 개별 조회 메서드
    public Workflow findWorkflow(Long workflowId) {
        return findVerifiedWorkflow(workflowId);
    }

    //아마 결재문서 삭제되면 함께 삭제될 것이라 직접적으로 삭제하는 일은 없을 듯
    //Approval 개별 삭제 메서드 , 필요없을 시 지울 예정
    public void deleteWorkflow (Long workflowId) {
        workflowRepository.delete(findVerifiedWorkflow(workflowId));
    }

    //Approval 개별 조회 메서드
    public Approval findApproval (Long approvalId) {
        return findVerifiedApproval(approvalId);
    }

    private int calculateNextStep(Workflow workflow, Approval.ApprovalStatus status) {
        // 워크플로우 단계 계산
        // 승인 시 +1, 전결시 마지막 단계
        // 반려 또는 협의요청 시 계산 없음.

        if(status == Approval.ApprovalStatus.APPROVE) {
            return workflow.getCurrentStep() + 1;
        } else if (status == Approval.ApprovalStatus.FINALIZE) {
            return workflow.getApprovals().size();
        } else {
            return workflow.getCurrentStep();
        }

    }

    private Workflow updateWorkflowStep(Workflow workflow, Approval.ApprovalStatus status) {
        //결재 단계 수정
        int nextStep = calculateNextStep(workflow, status);
        workflow.setCurrentStep(nextStep);

       return workflowRepository.save(workflow);
    }

    // DB에 id를 통해 해당 approval 이 있는지 검증 후 반환 메서드
    private Approval findVerifiedApproval(Long approvalId) {
        return approvalRepository.findById(approvalId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.APPROVAL_NOT_FOUND));
    }

    // DB에 id를 통해 해당 workflow 이 있는지 검증 후 반환 메서드
    private Workflow findVerifiedWorkflow (Long workflowId) {
        return workflowRepository.findById(workflowId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.WORKFLOW_NOT_FOUND));
    }

    public UserResponse getEmployee (Long employeeId) {
       return authServiceClient.getEmployeeByIdForUser(employeeId);
    }

    //검증된 회원인지 확인
    private void verifiedMember (Long employeeId) {
        UserResponse employee = getEmployee(employeeId);
        if (employee == null) {
            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
        }
    }

    private void updateDocumentStatus(Workflow workflow) {
        Document document = workflow.getDocument();

        if (isAllApproved(workflow) || isFinalized(workflow)) {
            document.setDocumentStatus(Document.DocumentStatus.APPROVED);
            String message = String.format("전자결재 문서[%s] 가 승인되었습니다.", document.getDocumentCode());
            sendNotificationToAuthor(document.getEmployeeId(),message, document.getId());

        } else if (isAnyRejected(workflow)) {
            document.setDocumentStatus(Document.DocumentStatus.REJECTED);
            String message = String.format("전자결재 문서[%s] 가 반려되었습니다.", document.getDocumentCode());
            sendNotificationToAuthor(document.getEmployeeId(), message, document.getId());
        } else {
            document.setDocumentStatus(Document.DocumentStatus.IN_PROGRESS);
        }

        documentService.updateDocument(document);
    }

    private boolean isAllApproved(Workflow workflow) {
        return workflow.getApprovals().stream()
                .allMatch(approval -> approval.getApprovalStatus() == Approval.ApprovalStatus.APPROVE);
    }

    private boolean isAnyRejected(Workflow workflow) {
        return workflow.getApprovals().stream()
                .anyMatch(approval -> approval.getApprovalStatus() == Approval.ApprovalStatus.REJECT);
    }

    private boolean isFinalized(Workflow workflow) {
        return workflow.getApprovals().stream()
                .anyMatch(approval -> approval.getApprovalStatus() == Approval.ApprovalStatus.FINALIZE);
    }

    private void sendNotificationToAuthor(Long authorId, String message, Long documentId) {

        approvalProducer.sendDocumentNotification(authorId, message, documentId);
    }

}
