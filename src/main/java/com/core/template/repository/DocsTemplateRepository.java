package com.core.template.repository;

import com.core.template.entity.DocumentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocsTemplateRepository extends JpaRepository <DocumentTemplate, Long> {
    Boolean existsByTemplateName(String templateName);
}
