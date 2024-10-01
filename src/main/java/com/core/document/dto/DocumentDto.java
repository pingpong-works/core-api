package com.core.document.dto;

import com.core.document.entity.Document;
import lombok.Getter;

import java.time.LocalDateTime;

public class DocumentDto {

    @Getter
    public static class Post {
        private Long documentTypeId;
        private String title;
        private String content;
        private String author;
    }

    @Getter
    public static class Patch {
        private String title;
        private String content;
    }

    @Getter
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private LocalDateTime createdAt;
        private Document.DocumentStatus documentStatus;
        private String documentType;
    }
}
