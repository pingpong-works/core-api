package com.core.workflow.mapper;


import com.core.approval.entity.Approval;
import com.core.approval.mapper.ApprovalMapper;
import com.core.workflow.dto.WorkflowDto;
import com.core.workflow.entity.Workflow;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = ApprovalMapper.class)
public interface WorkflowMapper {

    default Workflow postDtoToWorkflow(WorkflowDto.Post postDto) {
        Workflow workflow = Workflow.builder().build();

        List<Approval> approvals = postDto.getApprovals().stream()
                        .map(approval1-> {
                            Approval approval = new Approval();
                            approval.setApprovalOrder(approval1.getApprovalOrder());
                            approval.setEmployeeId(approval1.getEmployeeId());
                            approval.setWorkflow(workflow);
                            return approval;
                        }).collect(Collectors.toList());
        workflow.setApprovals(approvals);

        return workflow;
    }

    Workflow patchDtoToWorkflow(WorkflowDto.Patch patchDto);
    WorkflowDto.Response approvalToResponse(Workflow workflow);
    List<WorkflowDto.Response> approvalsToResponses(List<Workflow> workflows);
}
