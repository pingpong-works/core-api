package com.core.document.service;

import com.core.document.entity.Document;
import com.core.document.repository.DocumentRepository;
import com.core.utils.PageableCreator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {
    private final DocumentRepository repository;

    public DocumentService(DocumentRepository repository) {
        this.repository = repository;
    }

    public Document createDocument(Document document) {

        return document;
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
