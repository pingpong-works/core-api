package com.core.document.service;

import com.core.client.auth.AuthServiceClient;
import com.core.client.auth.EmployeeDto;
import com.core.document.dto.DocumentDto;
import com.core.document.entity.Document;
import com.core.document.repository.DocumentRepository;
import com.core.document.repository.DocumentRepositoryCustom;
import com.core.exception.BusinessLogicException;
import com.core.exception.ExceptionCode;
import com.core.kfaka.ApprovalProducer;
import com.core.type.entity.DocumentType;
import com.core.type.service.DocsTypeService;
import com.core.utils.PageableCreator;
import com.core.workflow.entity.Workflow;
import com.core.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final DocsTypeService docsTypeService;
    private final WorkflowRepository workflowRepository;
    private final AuthServiceClient authServiceClient;
    private final DocumentRepositoryCustom documentRepositoryCustom;
    private final ApprovalProducer approvalProducer;

    //전자결재서류 생성 - 임시저장
    public Document createDocument(Document document) {

        //workflow id, documentType id 검증
        DocumentType documentType = docsTypeService.findDocsType(document.getDocumentType().getId());

        Workflow workflow = null;
        if (document.getWorkflow().getId() != null) {
            workflow = workflowRepository.findById(document.getWorkflow().getId())
                    .orElseThrow(() -> new BusinessLogicException(ExceptionCode.WORKFLOW_NOT_FOUND));
            document.setWorkflow(workflow);
        }

        EmployeeDto employee = verifiedEmployee(document.getEmployeeId());


        document.setDocumentType(documentType);
        document.setAuthor(employee.getName());
        document.setDepartmentName(employee.getDepartmentName());
        document.setWorkflow(workflow);

        return documentRepository.save(document);
    }

    //전자결재 - 제출
    public Document submitDocument(Document document) {

        //workflow id, documentType id 검증
        DocumentType documentType = docsTypeService.findDocsType(document.getDocumentType().getId());
        Workflow workflow = workflowRepository.findById(document.getWorkflow().getId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.WORKFLOW_NOT_FOUND));

        EmployeeDto employee = verifiedEmployee(document.getEmployeeId());

        document.setDocumentType(documentType);
        document.setWorkflow(workflow);
        document.setAuthor(employee.getName());
        document.setDepartmentName(employee.getDepartmentName());
        document.setDocumentCode(createdDocumentCode(document));
        document.setDocumentStatus(Document.DocumentStatus.IN_PROGRESS);

        //전체 승인자에게 알림 전송
        workflow.getApprovals().forEach(approval -> {
            approvalProducer.sendApprovalNotification(
                    approval.getEmployeeId(),
                    "전자결재 문서가 도착했습니다",
                    approval.getId()
            );
        });


        return documentRepository.save(document);
    }

    public Document updateDocument(Document document) {

        return documentRepository.save(document);
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

        EmployeeDto employee = verifiedEmployee(employeeId);

        //관리자가 아닐 경우 본인이 작성한 글만 삭제 가능
        if(findDocument.getEmployeeId() != employee.getId()) {
            throw new BusinessLogicException(ExceptionCode.DO_NOT_HAVE_PERMISSION);
        }

        documentRepository.delete(findDocument);
    }

    private Document findVerifiedDocument (Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DOCUMENT_NOT_FOUND));
    }

    //외부에서 employee 가져오기
    public EmployeeDto getEmployee (Long employeeId) {
        return authServiceClient.getEmployeeById(employeeId);
    }

    //검증된 회원인지 확인
    public EmployeeDto verifiedEmployee(Long employeeId) {
        EmployeeDto employee = getEmployee(employeeId);
        if (employee == null) {
            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND);
        }
        return employee;
    }

    //문서번호 생성
    private String createdDocumentCode (Document document) {
        String docsType = document.getDocumentType().getType();
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 5).toUpperCase();

       // String formattedId = String.format("%05d", document.getId());

        return docsType.substring(0, 2) + "-" + uuid + "-" ;//+ formattedId;
    }
}
