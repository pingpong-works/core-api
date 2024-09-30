package com.core.type.service;

import com.core.type.entity.DocumentType;
import com.core.type.repository.DocsTypeRepository;
import com.core.utils.PageableCreator;
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

    public DocumentType createDocsType(DocumentType docsType) {

        return docsType;
    }

    public DocumentType updateDocsType(DocumentType docsType) {
        return docsType;
    }

    // 개별 조회
    public DocumentType findDocsType(Long typeId) {
        return findVerifiedDocsType(typeId);
    }

    // 전체 조회 (pagination)
    public Page<DocumentType> findDocsTypes(int page, int size , String criteria, String direction) {

        Pageable pageable = PageableCreator.createPageable(page, size, criteria, direction);

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
