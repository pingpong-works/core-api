package com.core.approval.dto;

import com.core.approval.entity.Approval;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ApprovalDto {
    @Getter
    public static class Post {
        private Long employeeId; //결재자
        private int approvalOrder;
    }
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    public static class Patch {
        private Long id;
        private int approvalOrder;
        private Approval.ApprovalStatus approvalStatus;
        private String message;
        private Long employeeId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long employeeId;
        private int approvalOrder;
        private Approval.ApprovalStatus approvalStatus;
    }
}
