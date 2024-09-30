package com.core.document;

import com.springboot.type.dto.DocsTypeDto;
import com.springboot.type.entity.DocumentType;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DocsMapper {
    DocumentType postDtoToDocsType(DocsTypeDto.Post postDto);
    DocumentType patchDtoToDocsType(DocsTypeDto.Patch patchDto);
    DocsTypeDto.Response docsTypeToResponse(DocumentType type);
    List<DocsTypeDto.Response> docsTypesToResponses(List<DocumentType> docsTypes);

}
