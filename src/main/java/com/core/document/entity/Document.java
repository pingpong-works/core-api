package com.core.document.entity;

import com.core.type.entity.DocumentType;
import com.core.workflow.entity.Workflow;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String title;

    @Column
    private String content;

    @Column
    private String author;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private DocumentStatus documentStatus = DocumentStatus.DRAFT;

    @OneToOne
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;

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
