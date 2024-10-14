package com.core.type.mapper;

import com.core.template.entity.DocumentTemplate;
import com.core.template.entity.TemplateField;
import com.core.type.dto.DocsTypeDto;
import com.core.type.entity.DocumentType;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DocsTypeMapper {
    default DocumentType postDtoToDocsType(DocsTypeDto.Post postDto) {
        DocumentType.DocumentTypeBuilder docsTypeBuilder = DocumentType.builder();

        DocumentTemplate documentTemplate;

        if (postDto.getTemplateId() != null && postDto.getTemplateId() > 0) {
            documentTemplate = DocumentTemplate.builder()
                    .id(postDto.getTemplateId())
                    .build();
            docsTypeBuilder.documentTemplate(documentTemplate);
        }

        docsTypeBuilder.type(postDto.getType());

        return docsTypeBuilder.build();
    }

    default DocumentType postDtoWithNewTemplateToDocsType (DocsTypeDto.PostWithNewTemplate postDto) {
        DocumentTemplate template = DocumentTemplate.builder()
                .templateName(postDto.getTemplateDto().getTemplateName())
                .fields(postDto.getTemplateDto().getFields().stream().map(field -> {
                    TemplateField templateField = new TemplateField();
                    templateField.setFieldType(field.getFieldType());
                    templateField.setFieldName(field.getFieldName());
                    return templateField;
                }).collect(Collectors.toList())).build();

        return DocumentType.builder()
                .type(postDto.getType())
                .documentTemplate(template)
                .build();
    }

    DocumentType patchDtoToDocsType(DocsTypeDto.Patch patchDto);
    DocsTypeDto.Response docsTypeToResponse(DocumentType type);
    List<DocsTypeDto.Response> docsTypesToResponses(List<DocumentType> docsTypes);

}
