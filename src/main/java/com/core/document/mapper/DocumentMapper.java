package com.core.document.mapper;

import com.core.approval.dto.ApprovalDto;
import com.core.document.dto.DocumentDto;
import com.core.document.entity.Document;
import com.core.template.dto.DocsTemplateDto;
import com.core.template.dto.FieldDto;
import com.core.template.entity.DocumentTemplate;
import com.core.type.dto.DocsTypeDto;
import com.core.type.entity.DocumentType;
import com.core.workflow.dto.WorkflowDto;
import com.core.workflow.entity.Workflow;
import lombok.Builder;
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
        document.author(postDto.getAuthor());
        return document.build();
    }
    Document patchDtoToDocument(DocumentDto.Patch patchDto);

    default DocumentDto.Response documentToResponse(Document document) {
        // Document 기본 정보 설정
        DocumentDto.Response response = DocumentDto.Response.builder()
                .id(document.getId())
                .author(document.getAuthor())
                .documentStatus(document.getDocumentStatus())
                .createdAt(document.getCreatedAt())
                .title(document.getTitle())
                .content(document.getContent())
                .build();

        // DocsTypeDto 설정
        DocsTypeDto.Response docsTypeResponse = DocsTypeDto.Response.builder()
                .id(document.getDocumentType().getId())
                .type(document.getDocumentType().getType())
                .templates(
                        new DocsTemplateDto.Response(
                                document.getDocumentType().getDocumentTemplate().getId(),
                                document.getDocumentType().getDocumentTemplate().getTemplateName(),
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

        // WorkflowDto 설정
        WorkflowDto.Response workflowResponse = WorkflowDto.Response.builder()
                .workflowId(document.getWorkflow().getId())
                .currentStep(document.getWorkflow().getCurrentStep())
                .build();

        // 승인자 정보 설정
        List<ApprovalDto.Response> approvals = document.getWorkflow().getApprovals().stream()
                .map(approval -> {
                    return ApprovalDto.Response.builder()
                            .employeeId(approval.getEmployeeId())
                            .approvalOrder(approval.getApprovalOrder())
                            .approvalStatus(approval.getApprovalStatus())
                            .build();
                }).collect(Collectors.toList());

        workflowResponse.setApprovals(approvals);

        // 완성된 Response 객체 반환
        response.setWorkFlow(workflowResponse);
        response.setDocsTypes(docsTypeResponse);

        return response;
    }

    List<DocumentDto.Response> documentsToResponses(List<Document> documents);
}
