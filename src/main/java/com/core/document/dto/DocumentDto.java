package com.core.document.dto;

import com.core.document.entity.Document;
import com.core.type.dto.DocsTypeDto;
import com.core.workflow.dto.WorkflowDto;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
        @Setter
        private Long id;
        private Long workflowId;
        private String title;
        private String content;
        private Map<String,Object> customFields;
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
        private String documentCode;
        private Long employeeId;
        //docsTemplate
        private DocsTypeDto.Response docsTypes;

        //workflow
        private WorkflowDto.Response workFlow;
    }

    @Builder
    @Getter
    public static class DocumentSearch {
        @DateTimeFormat(pattern = "yyyyMMdd")
        private LocalDate searchStartDate = LocalDate.of(1900, 1, 1);  // 기본값

        @DateTimeFormat(pattern = "yyyyMMdd")
        private LocalDate searchEndDate = LocalDate.of(9999, 12, 31); // 기본값

        private String departmentName;
        private String author;
        private Document.DocumentStatus documentStatus;
        private String title;
        private String type;
    }

    @Getter
    @NoArgsConstructor
    public static class Delete {

        @NotNull(message = "deleteIds의 List는 null일 수 없습니다.")
        @NotEmpty(message = "deleteIds의 List는 비어있으면 안됩니다.")
        @Size(min = 1, message = "deleteIds의 List는 최소 1개 이상이어야 합니다.")
        private List<Long> deleteIds;
    }
}
