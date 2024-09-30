package com.core.type.controller;

import com.core.type.dto.DocsTypeDto;
import com.core.type.entity.DocumentType;
import com.core.type.mapper.DocsTypeMapper;
import com.core.type.service.DocsTypeService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/documents/types")
public class DocsTypeController {
    private final DocsTypeService service;
    private final DocsTypeMapper mapper;

    public DocsTypeController(DocsTypeService service, DocsTypeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postDocument (@RequestBody DocsTypeDto.Post postDto) {

        DocumentType docsType = service.createDocument(mapper.postDtoToDocsType(postDto));

        return new ResponseEntity(mapper.docsTypeToResponse(docsType), HttpStatus.OK);
    }

    @PatchMapping("/{documentId}")
    public ResponseEntity patchDocument (@RequestBody DocsTypeDto.Patch patchDto) {

        DocumentType docsType = service.updateDocument(mapper.patchDtoToDocsType(patchDto));

        return new ResponseEntity(mapper.docsTypeToResponse(docsType), HttpStatus.OK);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity getDocument (@PathVariable("documentId") @Positive Long documentId) {

        return new ResponseEntity<>(mapper
                .docsTypeToResponse(service.findDocument(documentId)),HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getDocuments (@Positive @RequestParam int page, @Positive @RequestParam int size ) {

        Page<DocumentType> typePage = service.findDocuments(page, size);
        List<DocumentType> typeList = typePage.getContent();

        return new ResponseEntity(mapper.docsTypesToResponses(typeList), HttpStatus.OK);
    }

    @DeleteMapping("/{type-id}")
    public ResponseEntity deleteDocument (@PathVariable("type-id") @Positive Long typeId) {

        service.deleteDocsType(typeId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
