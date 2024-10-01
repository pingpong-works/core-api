package com.core.document.service;

import com.core.document.entity.Document;
import com.core.document.repository.DocumentRepository;
import com.core.type.entity.DocumentType;
import com.core.type.service.DocsTypeService;
import com.core.utils.PageableCreator;
import com.core.workflow.entity.Workflow;
import com.core.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository repository;
    private final DocsTypeService docsTypeService;
    private final WorkflowService workflowService;


    //전자결재서류 생성
    public Document createDocument(Document document) {

        //workflow id, documentType id 검증
        DocumentType documentType = docsTypeService.findDocsType(document.getDocumentType().getId());
        Workflow workflow = workflowService.findWorkflow(document.getWorkflow().getId());

        document.setDocumentType(documentType);
        document.setWorkflow(workflow);

        return repository.save(document);
    }

    public Document updateDocument(Document document) {
        return document;
    }

    // 개별 조회
    public Document findDocument(Long documentId) {
        return findVerifiedDocument(documentId);
    }

    // 전체 조회 (pagination)
    public Page<Document> findDocuments(int page, int size ,String criteria, String direction) {

        Pageable pageable = PageableCreator.createPageable(page, size, criteria, direction);

        return repository.findAll(pageable);
    }

    // 개별 삭제
    public void deleteDocument(Long documentId) {

        repository.delete(findVerifiedDocument(documentId));
    }

    private Document findVerifiedDocument (Long documentId) {
        return repository.findById(documentId)
                .orElseThrow(RuntimeException::new);
    }

}
