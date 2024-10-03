package com.core.document.dto;

import com.core.document.entity.Document;
import com.core.type.dto.DocsTypeDto;
import com.core.workflow.dto.WorkflowDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;


public class DocumentDto {

    @Getter
    @Builder
    public static class Post {
        private Long documentTypeId;
        private Long workflowId;
        private String title;
        private String content;
        private Long employeeId;
        private Map<String,Object> customFields;
    }

    @Getter
    @NoArgsConstructor
    public static class Patch {
        private String title;
        private String content;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Setter
    public static class Response {
        private Long id;
        private String author;
        private String departmentName;
        private String title;
        private String content;
        private LocalDateTime createdAt;
        private Document.DocumentStatus documentStatus;
        private Map<String,Object> customFields;
        //docsTemplate
        private DocsTypeDto.Response docsTypes;

        //workflow
        private WorkflowDto.Response workFlow;
    }
}
