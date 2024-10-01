package com.core.approval.mapper;

import com.core.approval.dto.ApprovalDto;
import com.core.approval.entity.Approval;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ApprovalMapper {
    Approval postDtoToApproval(ApprovalDto.Post postDto);
    ApprovalDto.Response approvalToResponse(Approval approval);
    List<ApprovalDto.Response> approvalsToResponses(List<Approval> approvals);
}
