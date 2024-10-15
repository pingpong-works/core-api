package com.core.type.dto;

import com.core.template.dto.DocsTemplateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

public class DocsTypeDto {

    @Builder
    @Getter
    public static class Post {
        @Min(value = 1, message = "Id는 1 이상이어야 합니다.")
        private Long templateId;
        @NotNull(message = "type이 누락되었습니다.")
        private String type;
    }

    @Builder
    @Getter
    public static class PostWithNewTemplate {
        private DocsTemplateDto.Post templateDto;
        private String type;
    }

    @Builder
    @Getter
    public static class Patch {
        @Setter
        private Long id;
        private Boolean isVisible;
        private String type;
    }

    @Builder
    @Getter
    public static class Response {
        private Long id;
        private Boolean isVisible;
        private String type;
        private DocsTemplateDto.Response documentTemplate;
    }

}
