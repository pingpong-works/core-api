package com.core.document.entity;

import com.core.type.entity.DocumentType;
import com.core.utils.MapToConverter;
import com.core.workflow.entity.Workflow;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String title;

    @Column
    private String content;

    @Column
    private Long employeeId;

    @Column
    private String departmentName;

    @Column
    private String author;

    @Column
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DocumentStatus documentStatus = DocumentStatus.DRAFT;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;

    @Convert(converter = MapToConverter.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> customFields;

    @ManyToOne
    @JoinColumn(name = "document_type_id", nullable = false)
    private DocumentType documentType;

    public enum DocumentStatus {
        DRAFT("작성중"),
        IN_PROGRESS("결재중"),
        APPROVED("승인됨"),
        REJECTED("반려됨");

        @Getter
        @Setter
        private String description;

        DocumentStatus(String description) {
            this.description = description;
        }
    }
}
