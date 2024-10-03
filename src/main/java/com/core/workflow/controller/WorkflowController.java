package com.core.workflow.controller;

import com.core.approval.dto.ApprovalDto;
import com.core.approval.mapper.ApprovalMapper;
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
    private final WorkflowMapper workflowMapper;
    private final ApprovalMapper approvalMapper;
    private static final String WORKFLOW_DEFAULT_URL = "/workflows";

    public WorkflowController(WorkflowService service, WorkflowMapper workflowMapper, ApprovalMapper approvalMapper) {
        this.service = service;
        this.workflowMapper = workflowMapper;
        this.approvalMapper = approvalMapper;
    }

    @PostMapping
    public ResponseEntity postWorkflow (@RequestBody WorkflowDto.Post postDto) {

        Workflow createWorkflow = service.createWorkflow(workflowMapper.postDtoToWorkflow(postDto));

        URI location = UriCreator.createUri(WORKFLOW_DEFAULT_URL, createWorkflow.getId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping
    public ResponseEntity patchWorkflow (@RequestBody ApprovalDto.Patch patchDto) {

        Workflow workflow = service.updateWorkflow(approvalMapper.patchToApproval(patchDto));

        return new ResponseEntity(
                new SingleResponseDto<>(workflowMapper.approvalToResponse(workflow)), HttpStatus.OK);
    }

    @GetMapping("/{workflow-id}")
    public ResponseEntity getWorkflow (@PathVariable("workflow-id") @Positive Long workflowId ) {

        return new ResponseEntity(
                new SingleResponseDto<>(workflowMapper.approvalToResponse(service.findWorkflow(workflowId))),HttpStatus.OK);
    }

    @DeleteMapping("/{workflow-id}")
    public ResponseEntity deleteWorkflow (@PathVariable("workflow-id") @Positive Long workflowId ) {

        service.deleteWorkflow(workflowId);

        return ResponseEntity.noContent().build();
    }

}
