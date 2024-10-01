package com.core.template.mapper;

import com.core.template.dto.DocsTemplateDto;
import com.core.template.entity.DocumentTemplate;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DocsTemplateMapper {
    DocumentTemplate postDtoToDocsTemplate(DocsTemplateDto.Post postDto);
    DocumentTemplate patchDtoToDocsTemplate(DocsTemplateDto.Patch patchDto);
    DocsTemplateDto.Response templateToResponse(DocumentTemplate template);
    List<DocsTemplateDto.Response> templatesToResponses(List<DocumentTemplate> templates);

}
