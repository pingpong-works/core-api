package com.core.template.service;

import com.core.template.entity.DocumentTemplate;
import com.core.template.repository.DocsTemplateRepository;
import com.core.utils.PageableCreator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DocsTemplateService {
    private final DocsTemplateRepository repository;

    public DocsTemplateService(DocsTemplateRepository repository) {

        this.repository = repository;
    }

    public DocumentTemplate createTemplate(DocumentTemplate template) {

        return repository.save(template);
    }

    public DocumentTemplate updateTemplate(DocumentTemplate template) {

        return repository.save(template);
    }

    public Page<DocumentTemplate> findTemplates(int page, int size, String sort, String direction) {

        Pageable pageable = PageableCreator.createPageable(page, size, sort, direction);

        return repository.findAll(pageable);
    }

    public DocumentTemplate findTemplate(Long templateId) {

        return findVerifiedTemplate(templateId);
    }

    public void deleteTemplate (Long templateId) {

        repository.delete(findVerifiedTemplate(templateId));
    }

    private DocumentTemplate findVerifiedTemplate(Long templateId) {
        return repository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));
    }
}
