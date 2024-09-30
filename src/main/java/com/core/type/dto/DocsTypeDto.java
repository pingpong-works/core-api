package com.core.document-ty

import com.core.document.entity.Document;
import lombok.Getter;

import java.time.LocalDateTime;

public class DocsTypeDto {
    @Getter
    public static class Post {
        private String type;
        private String content;
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
