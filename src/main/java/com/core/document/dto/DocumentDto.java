package com.core.document.dto;

import com.core.approval.dto.ApprovalDto;
import com.core.document.entity.Document;
import com.core.template.dto.FieldDto;
import com.core.type.dto.DocsTypeDto;
import com.core.workflow.dto.WorkflowDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class DocumentDto {

    @Getter
    @Builder
    public static class Post {
        private Long documentTypeId;
        private Long workflowId;
        private String title;
        private String content;
        private String author;
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
        private String title;
        private String content;
        private LocalDateTime createdAt;
        private Document.DocumentStatus documentStatus;

        //docsTemplate
        private DocsTypeDto.Response docsTypes;

        //workflow
        private WorkflowDto.Response workFlow;
    }
}
