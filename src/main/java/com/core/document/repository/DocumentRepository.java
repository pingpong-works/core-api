package com.core.document.repository;

import com.core.document.entity.Document;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    @EntityGraph(attributePaths = {"workflow", "workflow.approvals"})
    Optional<Document> findById(Long id);
}
