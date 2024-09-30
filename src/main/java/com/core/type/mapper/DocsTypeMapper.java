package com.core.type.mapper;

import com.core.type.dto.DocsTypeDto;
import com.core.type.entity.DocumentType;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DocsTypeMapper {
    DocumentType postDtoToDocsType(DocsTypeDto.Post postDto);
    DocumentType patchDtoToDocsType(DocsTypeDto.Patch patchDto);
    DocsTypeDto.Response docsTypeToResponse(DocumentType type);
    List<DocsTypeDto.Response> docsTypesToResponses(List<DocumentType> docsTypes);

}
