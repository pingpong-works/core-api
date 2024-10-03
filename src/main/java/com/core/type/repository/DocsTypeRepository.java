package com.core.type.repository;

import com.core.type.entity.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocsTypeRepository extends JpaRepository <DocumentType, Long> {

    boolean existsByType(String type);
}
