package com.core.template.controller;

import com.core.exception.BusinessLogicException;
import com.core.exception.ExceptionCode;
import com.core.response.MultiResponseDto;
import com.core.template.dto.DocsTemplateDto;
import com.core.template.entity.DocumentTemplate;
import com.core.template.mapper.DocsTemplateMapper;
import com.core.template.service.DocsTemplateService;
import com.core.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Validated
@RestController
@RequestMapping("/templates")
public class DocsTemplateController {

    private final DocsTemplateService service;
    private final DocsTemplateMapper mapper;
    private final static String TEMPLATE_DEFAULT_URL = "/templates";

    public DocsTemplateController(DocsTemplateService service, DocsTemplateMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postTemplate(@Valid @RequestBody DocsTemplateDto.Post postDto) {

        DocumentTemplate createdTemplate = service.createTemplate(mapper.postDtoToDocsTemplate(postDto));

        return new ResponseEntity(mapper.docsTypeToPostResponse(createdTemplate),HttpStatus.CREATED);
    }

    @PatchMapping("/{template-id}")
    public ResponseEntity patchTemplate(@PathVariable("template-id") @Positive Long templateId,
                                        @Valid @RequestBody DocsTemplateDto.Patch patchDto) {

        patchDto.setId(templateId);

        DocumentTemplate updateTemplate = service.updateTemplate(mapper.patchDtoToDocsTemplate(patchDto));

        return new ResponseEntity(mapper.templateToResponse(updateTemplate),HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getTemplates(@RequestParam @Positive int page,
                                       @RequestParam @Positive int size,
                                       @RequestParam(required = false) String sort,
                                       @RequestParam(required = false) String direction) {
        String criteria = "id";

        if(sort != null) {
            List<String> sorts = Arrays.asList("templateName", "id");

            if (sorts.contains(sort)) {
                criteria = sort;
            } else {
                throw new BusinessLogicException(ExceptionCode.INVALID_SORT_FIELD);
            }
        }

        Page<DocumentTemplate> templatePage = service.findTemplates(page - 1 , size, criteria, direction);

        return new ResponseEntity<>( new MultiResponseDto(
                mapper.templatesToResponses(templatePage.getContent()),templatePage),HttpStatus.OK);
    }

    @GetMapping("/{template-id}")
    public ResponseEntity getTemplate (@PathVariable("template-id") @Positive Long templateId) {

        return new ResponseEntity(mapper.templateToResponse(service.findTemplate(templateId)), HttpStatus.OK);
    }

    @DeleteMapping("/{template-id}")
    public ResponseEntity deleteTemplate (@PathVariable("template-id") @Positive Long templateId) {

        return ResponseEntity.noContent().build();
    }
}
