package com.core.workflow.controller;

import com.core.response.SingleResponseDto;
import com.core.utils.UriCreator;
import com.core.workflow.dto.WorkflowDto;
import com.core.workflow.entity.Workflow;
import com.core.workflow.mapper.WorkflowMapper;
import com.core.workflow.service.WorkflowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.net.URI;


@Validated
@RestController
@RequestMapping("/workflows")
public class WorkflowController {

    private final WorkflowService service;
    private final WorkflowMapper mapper;
    private static final String WORKFLOW_DEFAULT_URL = "/workflows";

    public WorkflowController(WorkflowService service, WorkflowMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postWorkflow (@RequestBody WorkflowDto.Post postDto) {

        Workflow createWorkflow = service.createWorkflow(mapper.postDtoToWorkflow(postDto));

        URI location = UriCreator.createUri(WORKFLOW_DEFAULT_URL, createWorkflow.getId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{workflow-id}")
    public ResponseEntity patchWorkflow (@PathVariable("workflow-id") @Positive Long workflowId ,
                                         @RequestBody WorkflowDto.Patch patchDto) {
        patchDto.setId(workflowId);
        Workflow patchWorkflow = service.updateWorkflow(mapper.patchDtoToWorkflow(patchDto));

        return new ResponseEntity(
                new SingleResponseDto<>(mapper.approvalToResponse(patchWorkflow)), HttpStatus.OK);
    }

    @GetMapping("/{workflow-id}")
    public ResponseEntity getWorkflow (@PathVariable("workflow-id") @Positive Long workflowId ) {

        return new ResponseEntity(
                new SingleResponseDto<>(mapper.approvalToResponse(service.findWorkflow(workflowId))),HttpStatus.OK);
    }

    @DeleteMapping("/{workflow-id}")
    public ResponseEntity deleteWorkflow (@PathVariable("workflow-id") @Positive Long workflowId ) {

        service.deleteWorkflow(workflowId);

        return ResponseEntity.noContent().build();
    }

}
