package com.core.document.mapper;

import com.core.approval.dto.ApprovalDto;
import com.core.document.dto.DocumentDto;
import com.core.document.entity.Document;
import com.core.template.dto.DocsTemplateDto;
import com.core.template.dto.FieldDto;
import com.core.type.dto.DocsTypeDto;
import com.core.type.entity.DocumentType;
import com.core.workflow.dto.WorkflowDto;
import com.core.workflow.entity.Workflow;
import org.mapstruct.Mapper;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    default Document postDtoToDocument(DocumentDto.Post postDto) {

        DocumentType documentType = DocumentType.builder()
                        .id(postDto.getDocumentTypeId()).build();

        Workflow workflow = new Workflow();
        workflow.setId(postDto.getWorkflowId());

        Document.DocumentBuilder document = Document.builder();
        document.documentType(documentType);
        document.workflow(workflow);
        document.title(postDto.getTitle());
        document.content(postDto.getContent());
        document.employeeId(postDto.getEmployeeId());
        document.customFields(postDto.getCustomFields());
        return document.build();
    }

    default DocumentDto.Response documentToResponse(Document document) {
        // Document 기본 정보 설정
        DocumentDto.Response response = DocumentDto.Response.builder()
                .id(document.getId())
                .author(document.getAuthor())
                .departmentName(document.getDepartmentName())
                .documentStatus(document.getDocumentStatus())
                .createdAt(document.getCreatedAt())
                .title(document.getTitle())
                .content(document.getContent())
                .customFields(document.getCustomFields())
                .documentCode(document.getDocumentCode())
                .employeeId(document.getEmployeeId())
                .build();

        // DocsTypeDto 설정
        DocsTypeDto.Response docsTypeResponse = DocsTypeDto.Response.builder()
                .id(document.getDocumentType().getId())
                .type(document.getDocumentType().getType())
                .documentTemplate(
                        new DocsTemplateDto.Response(
                                document.getDocumentType().getDocumentTemplate().getId(),
                                document.getDocumentType().getDocumentTemplate().getTemplateName(),
                                document.getDocumentType().getDocumentTemplate().getVersion(),
                                document.getDocumentType().getDocumentTemplate().getFields().stream()
                                        .map(field -> new FieldDto.Response(
                                                field.getId(),
                                                field.getFieldName(),
                                                field.getFieldType()
                                        ))
                                        .collect(Collectors.toList())
                        )
                )
                .build();
        response.setDocsTypes(docsTypeResponse);

        // WorkflowDto 설정 - 임시저장 해야해서 null 검증 필요
        if (document.getWorkflow() != null) {
            WorkflowDto.Response workflowResponse = WorkflowDto.Response.builder()
                    .id(document.getWorkflow().getId())
                    .currentStep(document.getWorkflow().getCurrentStep())
                    .build();

            // 승인자 정보 설정 (null 검사 후 처리)
            if (document.getWorkflow().getApprovals() != null) {
                List<ApprovalDto.Response> approvals = document.getWorkflow().getApprovals().stream()
                        .map(approval -> ApprovalDto.Response.builder()
                                .id(approval.getId())
                                .employeeId(approval.getEmployeeId())
                                .approvalOrder(approval.getApprovalOrder())
                                .approvalStatus(approval.getApprovalStatus())
                                .message(approval.getMessage())
                                .build())
                        .collect(Collectors.toList());

                workflowResponse.setApprovals(approvals);
            }

            // 완성된 WorkflowResponse를 설정
            response.setWorkFlow(workflowResponse);
        }

        return response;
    }


    default Document patchDtoToDocument(DocumentDto.Patch patchDto) {

        Workflow workflow = new Workflow();
        workflow.setId(patchDto.getWorkflowId());

        Document.DocumentBuilder document = Document.builder();
        document.id(patchDto.getId());
        document.workflow(workflow);
        document.title(patchDto.getTitle());
        document.content(patchDto.getContent());
        document.customFields(patchDto.getCustomFields());
        return document.build();
    }

    List<DocumentDto.Response> documentsToResponses(List<Document> documents);
}
