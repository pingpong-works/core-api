package com.core.workflow.dto;

import com.core.approval.dto.ApprovalDto;
import com.core.approval.entity.Approval;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class WorkflowDto {

    @Getter
    public static class Post {
        private Long documentId;
        private int currentStep;
        private List<ApprovalDto.Post> approvals;
    }

    @Getter
    public static class Patch {
        @Setter
        private Long id;
        private int currentStep;
        private List<ApprovalDto.Patch> approvals;
    }

    @Getter
    public static class Response {
        private Long id;
        private int approvalOrder;
        private String approvalStatus;
        private Long employeeId;
        private Long workflowId;
    }

}
