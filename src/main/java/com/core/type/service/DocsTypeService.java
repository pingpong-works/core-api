package com.core.type.service;

import com.core.exception.BusinessLogicException;
import com.core.exception.ExceptionCode;
import com.core.template.entity.DocumentTemplate;
import com.core.template.service.DocsTemplateService;
import com.core.type.entity.DocumentType;
import com.core.type.repository.DocsTypeRepository;
import com.core.utils.PageableCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocsTypeService {
    private final DocsTypeRepository repository;
    private final DocsTemplateService docsTemplateService;


    public DocumentType createDocsTypeWithTemplate(DocumentType docsType) {

        // template 존재하는지 검증
        docsTemplateService.findTemplate(docsType.getDocumentTemplate().getId());

        return repository.save(docsType);
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
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DOCS_TYPE_NOT_FOUND));
    }

}
