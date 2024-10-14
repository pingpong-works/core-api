package com.core.template.service;

import com.core.exception.BusinessLogicException;
import com.core.exception.ExceptionCode;
import com.core.template.entity.DocumentTemplate;
import com.core.template.entity.TemplateField;
import com.core.template.repository.DocsTemplateRepository;
import com.core.utils.PageableCreator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class DocsTemplateService {
    private final DocsTemplateRepository repository;

    public DocsTemplateService(DocsTemplateRepository repository) {

        this.repository = repository;
    }

    public DocumentTemplate createTemplate(DocumentTemplate template) {
        verifyExistsTemplateName(template.getTemplateName());
        template.setCreatedAt(LocalDateTime.now());
        return repository.save(template);
    }

    //template 수정 기능 : 근데 template 는 수정되면 기존 양식들도 다 변경되니까 <새로운 템플릿 생성 해서 버전으로 관리>
    public DocumentTemplate updateTemplate(DocumentTemplate template) {

        // 기존 템플릿 조회
        DocumentTemplate oldTemplate = findVerifiedTemplate(template.getId());

        // 필드 변경 여부 확인
        boolean isFieldsChanged = false;

        for (TemplateField field : template.getFields()) {
            TemplateField existingField = oldTemplate.getFields().stream()
                    .filter(f -> f.getId().equals(field.getId()))
                    .findFirst()
                    .orElse(null);

            if (existingField == null ||
                    !existingField.getFieldName().equals(field.getFieldName()) ||
                    !existingField.getFieldType().equals(field.getFieldType())) {
                isFieldsChanged = true;
                break;
            }
        }

        // 필드가 변경되었거나 템플릿 이름이 변경되었을 때만 새 버전 생성
        if (!oldTemplate.getTemplateName().equals(template.getTemplateName()) || isFieldsChanged) {
            // 새 버전 생성
            DocumentTemplate newTemplate = DocumentTemplate.builder()
                    .templateName(template.getTemplateName() != null ? template.getTemplateName() : oldTemplate.getTemplateName())
                    .version(oldTemplate.getVersion() + 1)
                    .modifiedAt(LocalDateTime.now())
                    .fields(new ArrayList<>()) // 새로운 필드 리스트 생성
                    .build();

            // 필드 복사
            for (TemplateField field : template.getFields()) {
                TemplateField copiedField = TemplateField.builder()
                        .fieldName(field.getFieldName())
                        .fieldType(field.getFieldType())
                        .documentTemplate(newTemplate)
                        .build();

                newTemplate.getFields().add(copiedField);
            }

            // 새로운 버전의 템플릿을 저장
            return repository.save(newTemplate);
        } else {
            // 필드나 템플릿 이름에 변화가 없을 경우 기존 템플릿 반환
            return oldTemplate;
        }
    }

    public Page<DocumentTemplate> findTemplates(int page, int size, String sort, String direction) {

        Pageable pageable = PageableCreator.createPageable(page, size, sort, direction);

        return repository.findAll(pageable);
    }

    public DocumentTemplate findTemplate(Long templateId) {

        return findVerifiedTemplate(templateId);
    }

    //template 삭제 : 권한 = 관리자
    public void deleteTemplate (Long templateId) {

        repository.delete(findVerifiedTemplate(templateId));
    }

    private DocumentTemplate findVerifiedTemplate(Long templateId) {
        return repository.findById(templateId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.TEMPLATE_NOT_FOUND));
    }

    private void verifyExistsTemplateName (String templateName) {
        if(repository.existsByTemplateName(templateName)) {
            throw new BusinessLogicException(ExceptionCode.TEMPLATE_NAME_ALREADY_EXISTS);
        }
    }
}
