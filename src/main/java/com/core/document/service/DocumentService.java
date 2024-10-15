package com.core.document.service;

import com.core.approval.entity.Approval;
import com.core.approval.repository.ApprovalRepository;
import com.core.client.auth.AuthServiceClient;
import com.core.client.auth.EmployeeData;
import com.core.client.auth.UserResponse;
import com.core.document.dto.DocumentDto;
import com.core.document.entity.Document;
import com.core.document.repository.DocumentRepository;
import com.core.document.repository.DocumentRepositoryCustom;
import com.core.exception.BusinessLogicException;
import com.core.exception.ExceptionCode;
import com.alarm.kafka.ApprovalProducer;
import com.core.type.entity.DocumentType;
import com.core.type.service.DocsTypeService;
import com.core.utils.PageableCreator;
import com.core.workflow.entity.Workflow;
import com.core.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final DocsTypeService docsTypeService;
    private final WorkflowRepository workflowRepository;
    private final AuthServiceClient authServiceClient;
    private final DocumentRepositoryCustom documentRepositoryCustom;
    private final ApprovalProducer approvalProducer;
    private final ApprovalRepository approvalRepository;

    //전자결재서류 생성 - 임시저장
    public Document createDocument(Document document) {

        //workflow id, documentType id 검증
        DocumentType documentType = docsTypeService.findDocsType(document.getDocumentType().getId());

        EmployeeData employee = verifiedEmployee(document.getEmployeeId());

        Workflow workflow = null;
        if ( document.getWorkflow().getId() != 0 ) {
            workflow = workflowRepository.findById(document.getWorkflow().getId())
                    .orElseThrow(() -> new BusinessLogicException(ExceptionCode.WORKFLOW_NOT_FOUND));

            //결재라인에 담당자 추가
            workflow = insertAuthor(workflow, employee.getEmployeeId());
            document.setWorkflow(workflow);

        }

        document.setDocumentType(documentType);
        document.setWorkflow(workflow);
        document.setAuthor(employee.getName());
        document.setDepartmentName(employee.getDepartmentName());
        document.setDocumentCode("생성중");

        return documentRepository.save(document);
    }

    //전자결재 - 제출
    public Document submitDocument(Document document) {

        //임시저장이 있다면 찾아와야 함.
        if(document.getId() != null) {
            findVerifiedDocument(document.getId());
        }

        //workflow id, documentType id 검증
        DocumentType documentType = docsTypeService.findDocsType(document.getDocumentType().getId());
        Workflow findWorkflow = workflowRepository.findById(document.getWorkflow().getId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.WORKFLOW_NOT_FOUND));

        EmployeeData employee = verifiedEmployee(document.getEmployeeId());


        //결재라인에 담당자 추가
        Workflow addApproval = insertAuthor(findWorkflow, employee.getEmployeeId());
        document.setWorkflow(addApproval);

        document.setDocumentType(documentType);
        document.setAuthor(employee.getName());
        document.setDepartmentName(employee.getDepartmentName());
        document.setDocumentCode(createdDocumentCode(document));
        document.setDocumentStatus(Document.DocumentStatus.IN_PROGRESS);

        //전체 승인자에게 알림 전송
        findWorkflow.getApprovals().forEach(approval -> {
            approvalProducer.sendApprovalNotification(
                    approval.getEmployeeId(),
                    String.format("전자결재 문서[%s] 가 도착했습니다.", document.getDocumentCode()),
                    approval.getId()
            );
        });

        return documentRepository.save(document);
    }

    // 임시저장 후 제출
    public Document updateDocument(Document document) {

        Document findDocument = findVerifiedDocument(document.getId());

        // Workflow 업데이트
        Optional.ofNullable(document.getWorkflow())
                .ifPresent(newWorkflow -> {
                    Workflow existingWorkflow = findDocument.getWorkflow();
                    if (existingWorkflow != null) {
                        // 기존 approvals 컬렉션 업데이트
                        List<Approval> existingApprovals = existingWorkflow.getApprovals();

                        // 새로운 Approval들의 ID 수집
                        Set<Long> newApprovalIds = newWorkflow.getApprovals().stream()
                                .filter(approval -> approval.getId() != null)
                                .map(Approval::getId)
                                .collect(Collectors.toSet());

                        // 기존 Approval 중에서 새로운 Approval에 없는 것들을 제거
                        existingApprovals.removeIf(approval -> !newApprovalIds.contains(approval.getId()));

                        // 새로운 Approval 추가 또는 기존 Approval 업데이트
                        for (Approval approval : newWorkflow.getApprovals()) {
                            if (approval.getId() == null) {
                                // 새로운 Approval인 경우
                                approval.setWorkflow(existingWorkflow);
                                existingApprovals.add(approval);
                            } else {
                                // 기존 Approval인 경우 필요한 필드 업데이트
                                Approval existingApproval = existingApprovals.stream()
                                        .filter(a -> a.getId().equals(approval.getId()))
                                        .findFirst()
                                        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.APPROVAL_NOT_FOUND));
                                // 필요한 필드 업데이트
                            }
                        }

                        // currentStep 등 다른 속성 업데이트
                        existingWorkflow.setCurrentStep(newWorkflow.getCurrentStep());

                    } else {
                        // 새로운 Workflow 설정
                        Workflow findWorkflow = workflowRepository.findById(document.getWorkflow().getId())
                                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.WORKFLOW_NOT_FOUND));

                        EmployeeData employee = verifiedEmployee(findDocument.getEmployeeId());

                        // 결재라인에 담당자 추가
                        Workflow addApproval = insertAuthor(findWorkflow, employee.getEmployeeId());
                        findDocument.setWorkflow(addApproval);
                    }
                });

        // 기타 필드 업데이트
        Optional.ofNullable(document.getTitle())
                .ifPresent(findDocument::setTitle);

        Optional.ofNullable(document.getContent())
                .ifPresent(findDocument::setContent);

        Optional.ofNullable(document.getCustomFields())
                .ifPresent(findDocument::setCustomFields);

        // DocumentCode 및 상태 업데이트
        findDocument.setDocumentCode(createdDocumentCode(findDocument));
        findDocument.setDocumentStatus(Document.DocumentStatus.IN_PROGRESS);

        //전체 승인자에게 알림 전송
        findDocument.getWorkflow().getApprovals().forEach(approval -> {
            approvalProducer.sendApprovalNotification(
                    approval.getEmployeeId(),
                    String.format("전자결재 문서[%s] 가 도착했습니다.", document.getDocumentCode()),
                    approval.getId()
            );
        });

        return documentRepository.save(findDocument);
    }

    // document 상태 수정
    public Document updateFieldDocument(Document document) {

        Document findDocument = findVerifiedDocument(document.getId());

        findDocument.setDocumentStatus(document.getDocumentStatus());

        return documentRepository.save(findDocument);
    }


    // 개별 조회
    public Document findDocument(Long documentId) {
        return findVerifiedDocument(documentId);
    }

    // 전체 조회 (pagination)
    public Page<Document> findDocuments(int page, int size , String criteria, String direction, DocumentDto.DocumentSearch search) {

        Pageable pageable = PageableCreator.createPageable(page, size, criteria, direction);

        return documentRepositoryCustom.findDocument(search, pageable);
    }

    // 개별 삭제 (관리자 또는 임시저장은 본인만)
    public void deleteDocument(Long documentId, Long employeeId) {
        Document findDocument = findVerifiedDocument(documentId);

        if(!findDocument.getDocumentStatus().equals(Document.DocumentStatus.DRAFT)) {
           throw new BusinessLogicException(ExceptionCode.DO_NOT_HAVE_PERMISSION);
        }

        EmployeeData employee = verifiedEmployee(employeeId);

        //관리자가 아닐 경우 본인이 작성한 글만 삭제 가능
        if(findDocument.getEmployeeId() != employee.getEmployeeId()) {
            throw new BusinessLogicException(ExceptionCode.DO_NOT_HAVE_PERMISSION);
        }

        documentRepository.delete(findDocument);
    }

    //검증된 문서인지 확인
    private Document findVerifiedDocument (Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DOCUMENT_NOT_FOUND));
    }

    //
    public UserResponse getEmployee(Long employeeId) {
        UserResponse response = authServiceClient.getEmployeeByIdForUser(employeeId);

        // 응답 데이터 확인
        log.debug("Response from Feign: {}", response); // 로그로 응답 출력

        if(response == null || response.getData() == null) {
            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
        }

        return response;
    }

    //검증된 회원인지 확인
    public EmployeeData verifiedEmployee(Long employeeId) {
        UserResponse employeeResponse = getEmployee(employeeId);

        EmployeeData employee = employeeResponse.getData();  // 내부의 data 필드 접근

        // null 체크 및 필수 필드 확인
        if (employee == null || employee.getName() == null || employee.getEmployeeId() == null || employee.getDepartmentName() == null) {
            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
        }

        return employee;
    }

    //문서번호 생성
    private String createdDocumentCode (Document document) {
        String docsType = document.getDocumentType().getType();
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();


        return docsType.substring(0, 2) + "-" + uuid;
    }

    private Workflow insertAuthor(Workflow workflow, Long employeeId) {
        // 승인자에 담당자 정보 저장
        Approval approval = new Approval();
        approval.setEmployeeId(employeeId);
        approval.setApprovalStatus(Approval.ApprovalStatus.APPROVE);
        approval.setApprovalOrder(0);

        approval.setWorkflow(workflow);

        // Approval 먼저 저장
        approval = approvalRepository.save(approval);

        // 저장된 approval을 workflow에 추가
        workflow.getApprovals().add(0, approval);
        //idx 0번으로 추가는 안되서 approvalOrder 순서 0으로 지정

        // workflow 저장
        return workflowRepository.save(workflow);
    }
}
