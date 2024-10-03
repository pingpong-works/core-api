package com.core.template.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DocsTemplateDto {
    @Getter
    public static class Post {
        private String templateName;
        private List<FieldDto.Post> fields;
    }

    @Getter
    public static class Patch {
        @Setter
        private Long id;
        private String templateName;
        private List<FieldDto.Patch> fields;
    }
    @AllArgsConstructor
    @Getter
    public static class Response {
        private Long id;
        private String templateName;
        private int version;
        private List<FieldDto.Response> fields;
    }
}
