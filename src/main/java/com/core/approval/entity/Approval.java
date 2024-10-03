package com.core.approval.entity;

import com.core.workflow.entity.Workflow;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Approval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "int default 1")
    private int approvalOrder; //결재순서

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "workflow_id", nullable = false)
    private Workflow workflow;

    private Long employeeId; //결재자

    @Column(length = 100)
    private String message; //결재자 메세지 (반려사유, 승인의견 등)

    public enum ApprovalStatus {
        PENDING("대기중"),
        APPROVE("승인"),
        REJECT("반려"),
        CONSULT("협의"),
        FINALIZE("전결");

        ApprovalStatus(String description) {this.description = description;}

        @Getter
        @Setter
        private String description;
    }
}
