package com.core.document.controller;

import com.core.document.dto.DocumentDto;
import com.core.document.entity.Document;
import com.core.document.mapper.DocumentMapper;
import com.core.document.service.DocumentService;
import com.core.exception.BusinessLogicException;
import com.core.exception.ExceptionCode;
import com.core.response.MultiResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import javax.validation.constraints.Positive;
import java.util.Arrays;
import java.util.List;

@Validated
@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentMapper mapper;
    private final DocumentService service;

    public DocumentController(DocumentMapper mapper, DocumentService service) {
        this.mapper = mapper;
        this.service = service;
    }

    @PostMapping
    public ResponseEntity postDocument (@RequestBody DocumentDto.Post postDto) {

        Document document = service.createDocument(mapper.postDtoToDocument(postDto));

        return new ResponseEntity(mapper.documentToResponse(document), HttpStatus.OK);
    }

    @PatchMapping("/{documentId}")
    public ResponseEntity patchDocument (@RequestBody DocumentDto.Patch patchDto) {

        Document document = service.updateDocument(mapper.patchDtoToDocument(patchDto));

        return new ResponseEntity(mapper.documentToResponse(document), HttpStatus.OK);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity getDocument (@PathVariable("documentId") @Positive Long documentId) {
        return new ResponseEntity<>(mapper
                .documentToResponse(service.findDocument(documentId)),HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getDocuments (@Positive @RequestParam int page,
                                        @Positive @RequestParam int size,
                                        @RequestParam(required = false) String sort,
                                        @RequestParam(required = false) String direction) {

        String criteria = "id";

        if(sort != null) {
            List<String> sorts = Arrays.asList("title", "createdAt", "id");

            if (sorts.contains(sort)) {
                criteria = sort;
            } else {
                throw new BusinessLogicException(ExceptionCode.INVALID_SORT_FIELD);
            }
        }

        Page<Document> documentPage = service.findDocuments(page - 1, size, criteria, direction);
        List<DocumentDto.Response> documentResponseList = mapper.documentsToResponses( documentPage.getContent());

        return new ResponseEntity<>(
                new MultiResponseDto<>(documentResponseList, documentPage), HttpStatus.OK);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity deleteDocument (@PathVariable("documentId") @Positive Long documentId) {

        service.deleteDocument(documentId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
