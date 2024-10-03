package com.core.document.service;

import com.core.client.auth.AuthServiceClient;
import com.core.client.auth.EmployeeDto;
import com.core.document.entity.Document;
import com.core.document.repository.DocumentRepository;
import com.core.exception.BusinessLogicException;
import com.core.exception.ExceptionCode;
import com.core.type.entity.DocumentType;
import com.core.type.service.DocsTypeService;
import com.core.utils.PageableCreator;
import com.core.workflow.entity.Workflow;
import com.core.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final DocsTypeService docsTypeService;
    private final WorkflowRepository workflowRepository;
    private final AuthServiceClient authServiceClient;


    //전자결재서류 생성
    public Document createDocument(Document document) {

        //workflow id, documentType id 검증
        DocumentType documentType = docsTypeService.findDocsType(document.getDocumentType().getId());
        Workflow workflow = workflowRepository.findById(document.getWorkflow().getId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.WORKFLOW_NOT_FOUND));

        EmployeeDto employee = verifiedEmployee(document.getEmployeeId());

        document.setDocumentType(documentType);
        document.setWorkflow(workflow);
        document.setAuthor(employee.getName());
        document.setDepartmentName(employee.getDepartmentName());

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
    public Page<Document> findDocuments(int page, int size ,String criteria, String direction) {

        Pageable pageable = PageableCreator.createPageable(page, size, criteria, direction);

        return documentRepository.findAll(pageable);
    }

    // 개별 삭제
    public void deleteDocument(Long documentId) {

        documentRepository.delete(findVerifiedDocument(documentId));
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

}
