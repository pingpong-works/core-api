package com.core.template.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

public class DocsTemplateDto {
    @Getter
    public static class Post {
        private String templateName;
        private List<FieldDto.Post> fields;
    }

    @Getter
    public static class Patch {
        private Long templateId;
        private String templateName;
    }
    @AllArgsConstructor
    @Getter
    public static class Response {
        private Long templateId;
        private String templateName;
        private List<FieldDto.Response> fields;
    }
}
