package com.core.exception;

import lombok.Getter;

public enum ExceptionCode {
    INVALID_SORT_FIELD(400,"Invalid Sort Field" ),
    INVALID_REQUEST(400, "Invalid request" ),
    APPROVAL_NOT_FOUND(404, "Approval Not Found"),
    WORKFLOW_NOT_FOUND(404, "Workflow Not Found" );

    @Getter
    private int status;
    @Getter
    private String message;

    ExceptionCode (int status, String message) {
        this.status = status;
        this.message = message;
    }
}
