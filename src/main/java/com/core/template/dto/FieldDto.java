package com.core.template.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

public class FieldDto {

    @Getter
    public static class Post {
        @NotNull(message = "fieldName이 누락되었습니다.")
        private String fieldName;
        @NotNull(message = "fieldType이 누락되었습니다.")
        private String fieldType;
    }


    @Getter
    public static class Patch {
        @Setter
        private Long templateFieldId;
        private String fieldName;
        private String fieldType;
    }

    @Getter
    public static class Response {
        private Long templateFieldId;
        private String fieldName;
        private String fieldType;
    }
}
