package com.core.approval.controller;

import com.core.approval.dto.ApprovalDto;
import com.core.approval.entity.Approval;
import com.core.approval.mapper.ApprovalMapper;
import com.core.approval.service.ApprovalService;
import com.core.response.MultiResponseDto;
import com.core.utils.UriCreator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import javax.validation.constraints.Positive;
import java.net.URI;


@Validated
@RestController
@RequestMapping("/approvals")
public class ApprovalController {
    private final ApprovalService service;
    private final ApprovalMapper mapper;
    private final static String APPROVAL_DEFAULT_URL = "/approvals";

    public ApprovalController(ApprovalService service, ApprovalMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postApproval (@RequestBody ApprovalDto.Post postDto) {
        Approval createApproval = service.createApproval(mapper.postDtoToApproval(postDto));
        URI location = UriCreator.createUri(APPROVAL_DEFAULT_URL, createApproval.getId());

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{approval-id}")
    public ResponseEntity getApproval (@PathVariable("approval-id") @Positive Long approvalId) {
        return new ResponseEntity(mapper.approvalToResponse(service.findApproval(approvalId)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getApprovals (@RequestParam @Positive int page,
                                        @RequestParam @Positive int size,
                                        @RequestParam(required = false) String direction) {


        Page<Approval> approvalPage = service.findApprovals(page, size, "id", direction);

        return new ResponseEntity(new MultiResponseDto(
                mapper.approvalsToResponses(approvalPage.getContent()), approvalPage) , HttpStatus.OK);

    }

}
