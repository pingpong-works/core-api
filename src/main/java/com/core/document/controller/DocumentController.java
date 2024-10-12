package com.core.document.controller;

import com.core.document.dto.DocumentDto;
import com.core.document.entity.Document;
import com.core.document.mapper.DocumentMapper;
import com.core.document.service.DocumentService;
import com.core.exception.BusinessLogicException;
import com.core.exception.ExceptionCode;
import com.core.response.MultiResponseDto;
import com.core.response.SingleResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import javax.validation.constraints.Positive;
import javax.ws.rs.Path;
import java.time.LocalDate;
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

    // 임시 저장
    @PostMapping("/save")
    public ResponseEntity saveDraftDocument(@RequestBody DocumentDto.Post postDto) {
        Document document = service.createDocument(mapper.postDtoToDocument(postDto));
        return new ResponseEntity(mapper.documentToResponse(document), HttpStatus.OK);
    }

    // 문서 제출
    @PostMapping("/submit")
    public ResponseEntity submitDocument(@RequestBody DocumentDto.Post postDto) {
        Document document = service.submitDocument(mapper.postDtoToDocument(postDto));
        return new ResponseEntity(mapper.documentToResponse(document), HttpStatus.OK);
    }

    // 임시저장 후 제출
    @PatchMapping("/{documentId}")
    public ResponseEntity patchDocument (@PathVariable("documentId") Long documentId, @RequestBody DocumentDto.Patch patchDto) {
        patchDto.setId(documentId);
        Document document = service.updateDocument(mapper.patchDtoToDocument(patchDto));

        return new ResponseEntity(mapper.documentToResponse(document), HttpStatus.OK);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity getDocument (@PathVariable("documentId") @Positive Long documentId) {
        return new ResponseEntity<>( new SingleResponseDto<>
                (mapper.documentToResponse(service.findDocument(documentId))),HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getDocuments (@Positive @RequestParam int page,
                                        @Positive @RequestParam int size,
                                        @RequestParam(required = false) String sort,
                                        @RequestParam(required = false) String direction,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                                        @RequestParam(required = false) String author,
                                        @RequestParam(required = false) String departmentName,
                                        @RequestParam(required = false) Document.DocumentStatus documentStatus,
                                        @RequestParam(required = false) String title,
                                        @RequestParam(required = false) String type) {


        String criteria = "id";

        if(sort != null) {
            List<String> sorts = Arrays.asList("title", "createdAt", "id", "documentType.type", "author" , "departmentName", "documentStatus");

            if (sorts.contains(sort)) {
                criteria = sort;
            } else {
                throw new BusinessLogicException(ExceptionCode.INVALID_SORT_FIELD);
            }
        }
        DocumentDto.DocumentSearch search = DocumentDto.DocumentSearch.builder()
                .searchStartDate(start)
                .searchEndDate(end)
                .author(author)
                .departmentName(departmentName)
                .documentStatus(documentStatus)
                .title(title)
                .type(type)
                .build();

        Page<Document> documentPage = service.findDocuments(page - 1, size, criteria, direction, search);
        List<DocumentDto.Response> documentResponseList = mapper.documentsToResponses( documentPage.getContent());

        return new ResponseEntity<>(
                new MultiResponseDto<>(documentResponseList, documentPage), HttpStatus.OK);
    }

    // 추후 보완예정
    @DeleteMapping("/{documentId}")
    public ResponseEntity deleteDocument (@PathVariable("documentId") @Positive Long documentId,
                                          Long employeeId) {

        service.deleteDocument(documentId, employeeId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity deleteDocuments (@RequestBody DocumentDto.Delete deleteIds,
                                          Long employeeId) {
        List<Long> ids =  deleteIds.getDeleteIds();

        for(Long id : ids) {
            service.deleteDocument(id, employeeId);
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


}
