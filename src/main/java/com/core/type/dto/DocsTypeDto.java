package com.core.type.dto;

import com.core.document.entity.Document;
import lombok.Getter;

import java.util.List;

public class DocsTypeDto {
    @Getter
    public static class Post {
        private String type;
    }

    @Getter
    public static class Patch {
        private String type;
    }

    @Getter
    public static class Response {
        private Long id;
        private String type;
        private List<Document> documents;
    }
}
