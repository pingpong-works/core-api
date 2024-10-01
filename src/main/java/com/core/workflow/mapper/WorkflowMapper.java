package com.core.workflow.mapper;


import com.core.workflow.dto.WorkflowDto;
import com.core.workflow.entity.Workflow;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkflowMapper {

    Workflow postDtoToApproval(WorkflowDto.Post postDto);
    Workflow patchDtoToApproval(WorkflowDto.Patch patchDto);
    WorkflowDto.Response approvalToResponse(Workflow workflow);
    List<WorkflowDto.Response> approvalsToResponses(List<Workflow> workflows);
}
