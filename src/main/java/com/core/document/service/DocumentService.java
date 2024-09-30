package com.core.document.service;

import com.core.document.entity.Document;
import com.core.document.repository.DocumentRepository;
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
    public Page<Document> findDocuments(int page, int size /*String criteria, String direction*/) {
        Pageable pageable = PageRequest.of(page,size);
        return repository.findAll(pageable);
    }

    public void deleteDocument(Long documentId) {

        repository.delete(findVerifiedDocument(documentId));
    }

    private Document findVerifiedDocument (Long documentId) {
        return repository.findById(documentId)
                .orElseThrow(RuntimeException::new);
    }

}
