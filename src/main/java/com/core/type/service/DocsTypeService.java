package com.core.type.service;

import com.core.type.entity.DocumentType;
import com.core.type.repository.DocsTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DocsTypeService {
    private final DocsTypeRepository repository;

    public DocsTypeService(DocsTypeRepository repository) {
        this.repository = repository;
    }

    public DocumentType createDocument(DocumentType docsType) {

        return docsType;
    }

    public DocumentType updateDocument(DocumentType docsType) {
        return docsType;
    }

    // 개별 조회
    public DocumentType findDocument(Long typeId) {
        return findVerifiedDocsType(typeId);
    }

    // 전체 조회 (pagination)
    public Page<DocumentType> findDocuments(int page, int size /*String criteria, String direction*/) {
        Pageable pageable = PageRequest.of(page,size);
        return repository.findAll(pageable);
    }

    public void deleteDocsType(Long typeId) {

        repository.delete(findVerifiedDocsType(typeId));
    }

    private DocumentType findVerifiedDocsType (Long typeId) {
        return repository.findById(typeId)
                .orElseThrow(RuntimeException::new);
    }

}
