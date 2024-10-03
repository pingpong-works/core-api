package com.core.document.repository;

import com.core.document.dto.DocumentDto;
import com.core.document.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface DocumentRepositoryCustom {
    Page<Document> findDocument(DocumentDto.DocumentSearch search, Pageable pageable) ;
}
