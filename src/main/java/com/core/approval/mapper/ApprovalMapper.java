package com.core.approval.mapper;

import com.core.approval.dto.ApprovalDto;
import com.core.approval.entity.Approval;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ApprovalMapper {

    Approval patchToApproval(ApprovalDto.Patch patchDto);
/*    default Approval patchToApproval(ApprovalDto.Patch patchDto) {
        Approval approval = Approval.builder()
                .approvalStatus(patchDto.getApprovalStatus())
                .message(patchDto.getMessage())
                .employeeId(patchDto.getEmployeeId())
                .id(patchDto.getId())
                .approvalOrder(patchDto.getApprovalOrder())
                .build();
        return approval;
    }*/
    ApprovalDto.Response approvalToResponse(Approval approval);
    List<ApprovalDto.Response> approvalsToResponses(List<Approval> approvals);
}
