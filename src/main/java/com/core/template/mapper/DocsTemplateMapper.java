package com.core.template.mapper;

import com.core.template.dto.DocsTemplateDto;
import com.core.template.dto.FieldDto;
import com.core.template.entity.DocumentTemplate;
import com.core.template.entity.TemplateField;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DocsTemplateMapper {
    default DocumentTemplate postDtoToDocsTemplate(DocsTemplateDto.Post postDto) {

        DocumentTemplate documentTemplate = DocumentTemplate.builder()
                .templateName(postDto.getTemplateName())
                .build();

        List<TemplateField> fields = postDto.getFields().stream().map(field -> {
            TemplateField templateField = new TemplateField();
            templateField.setFieldName(field.getFieldName());
            templateField.setFieldType(field.getFieldType());
            templateField.setDocumentTemplate(documentTemplate);
            return templateField;
        }).collect(Collectors.toList());

        documentTemplate.setFields(fields);

        return documentTemplate;
    }

    DocumentTemplate patchDtoToDocsTemplate(DocsTemplateDto.Patch patchDto);
    DocsTemplateDto.Response templateToResponse(DocumentTemplate template);
    List<DocsTemplateDto.Response> templatesToResponses(List<DocumentTemplate> templates);

    List<TemplateField> PostDtoToField (List<FieldDto.Post> postFieldDtos);
    FieldDto.Response templateFieldToResponse(TemplateField templateField);
    List<FieldDto.Response> fieldToResponse (List<TemplateField> fields);
}
