package com.core.exception;

import lombok.Getter;

public enum ExceptionCode {
    INVALID_SORT_FIELD(400,"Invalid Sort Field" ),
    INVALID_REQUEST(400, "Invalid request" ),
    APPROVAL_NOT_FOUND(404, "Approval Not Found"),
    WORKFLOW_NOT_FOUND(404, "Workflow Not Found" ),
    TEMPLATE_NAME_ALREADY_EXISTS(409, "Template Name Already Exists" ),
    EMPLOYEE_IN_APPROVAL_IS_DUPLICATE(409,"Employee in approval is duplicated" ),
    DOCS_TYPE_NOT_FOUND(404, "DocumentType Not Found" ),
    DOCS_TYPE_ALREADY_EXISTS(400, "Type Already Exists" ),
    TEMPLATE_NOT_FOUND(404, "Template Not Found" ),
    APPROVAL_PERMISSION_DENIED(403,"You do not have approval permissions" ),
    DOCUMENT_HAS_BEEN_FINALIZED(409,"The document has been finalized" ),
    DOCUMENT_HAS_BEEN_REJECTED(409,"The document has been rejected" ),
    DOCUMENT_NOT_FOUND(404, "Document Not Found" );

    @Getter
    private int status;
    @Getter
    private String message;

    ExceptionCode (int status, String message) {
        this.status = status;
        this.message = message;
    }
}
