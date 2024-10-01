package com.core.approval.entity;

import com.core.workflow.entity.Workflow;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Approval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int approvalOrder; //결재순서

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "workflow_id", nullable = false)
    private Workflow workflow;

    private Long employeeId; //결재자

    public enum ApprovalStatus {
        PENDING("대기중"),
        APPROVE("승인"),
        REJECT("반려");

        ApprovalStatus(String description) {this.description = description;}

        @Getter
        @Setter
        private String description;
    }
}
