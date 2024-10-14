package com.core.type.controller;

import com.core.exception.BusinessLogicException;
import com.core.exception.ExceptionCode;
import com.core.response.MultiResponseDto;
import com.core.type.dto.DocsTypeDto;
import com.core.type.entity.DocumentType;
import com.core.type.mapper.DocsTypeMapper;
import com.core.type.service.DocsTypeService;
import com.core.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/docs-types")
public class DocsTypeController {
    private final DocsTypeService service;
    private final DocsTypeMapper mapper;
    private final static String TYPE_DEFAULT_URL = "/docs-types";

    public DocsTypeController(DocsTypeService service, DocsTypeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postDocsType (@RequestBody DocsTypeDto.Post postDto) {

        DocumentType docsType = service.createDocsTypeWithTemplate(mapper.postDtoToDocsType(postDto));
        URI location = UriCreator.createUri(TYPE_DEFAULT_URL, docsType.getId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{type-id}")
    public ResponseEntity patchDocsType (@PathVariable("type-id") @Positive Long documentId ,
                                         @RequestBody DocsTypeDto.Patch patchDto) {
        patchDto.setId(documentId);
        DocumentType docsType = service.updateDocsType(mapper.patchDtoToDocsType(patchDto));

        return new ResponseEntity(mapper.docsTypeToResponse(docsType), HttpStatus.OK);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity getDocsType (@PathVariable("documentId") @Positive Long documentId) {

        return new ResponseEntity<>(mapper
                .docsTypeToResponse(service.findDocsType(documentId)),HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getDocsTypes (@Positive @RequestParam int page, @Positive @RequestParam int size,
                                        @RequestParam(required = false) String sort,
                                        @RequestParam(required = false) String direction) {

        String criteria = "id";

        if(sort != null) {
            List<String> sorts = Arrays.asList("type", "id");
            if (sorts.contains(sort)) {
                criteria = sort;
            } else {
                throw new BusinessLogicException(ExceptionCode.INVALID_SORT_FIELD);
            }
        }

        Page<DocumentType> typePage = service.findDocsTypes(page -1 , size, criteria, direction );
        List<DocsTypeDto.Response> typeResponseList = mapper.docsTypesToResponses(typePage.getContent());

        return new ResponseEntity<>(
                new MultiResponseDto<>(typeResponseList, typePage), HttpStatus.OK);
    }

    @DeleteMapping("/{type-id}")
    public ResponseEntity deleteDocsType (@PathVariable("type-id") @Positive Long typeId) {

        service.deleteDocsType(typeId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
