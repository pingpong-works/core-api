package com.core.document;

import com.springboot.type.entity.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocsTypeRepository extends JpaRepository <DocumentType, Long> {
}
