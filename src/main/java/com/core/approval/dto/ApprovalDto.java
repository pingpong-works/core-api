package com.core.approval.dto;

import com.core.approval.entity.Approval;

public class ApprovalDto {

    public static class Post {
        private Long employeeId; //결재자
    }

    public static class Patch {
        private Long id;
        private int approvalOrder;
        private String approvalStatus;
        private Long employeeId;
    }

    public static class Response {
        private Long employeeId;
        private int approvalOrder;
        private Approval.ApprovalStatus approvalStatus;
    }
}
