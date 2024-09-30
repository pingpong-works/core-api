package com.core.document.mapper;

import com.core.document.dto.DocumentDto;
import com.core.document.entity.Document;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    Document postDtoToDocument(DocumentDto.Post postDto);
    Document patchDtoToDocument(DocumentDto.Patch patchDto);
    DocumentDto.Response documentToResponse(Document document);
    List<DocumentDto.Response> documentsToResponses(List<Document> documents);
}
