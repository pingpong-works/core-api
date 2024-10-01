package com.core.approval.service;

import com.core.approval.entity.Approval;
import com.core.approval.repository.ApprovalRepository;
import com.core.exception.BusinessLogicException;
import com.core.exception.ExceptionCode;
import com.core.utils.PageableCreator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ApprovalService {
    private final ApprovalRepository repository;

    public ApprovalService(ApprovalRepository repository) {
        this.repository = repository;
    }


    //승인 또는 반려시
    public Approval updateApprovalStatus(Long approvalId, Approval.ApprovalStatus status) {
        Approval approval = findVerifiedApproval(approvalId);

        approval.setApprovalStatus(status);
        return repository.save(approval);
    }

    public Approval findApproval (Long approvalId) {
        return findVerifiedApproval(approvalId);
    }

    public Page<Approval> findApprovals (int page, int size, String sort, String direction) {

        Pageable pageable = PageableCreator.createPageable(page, size, sort, direction);

        return repository.findAll(pageable);
    }

    private Approval findVerifiedApproval(Long approvalId) {
        return repository.findById(approvalId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.APPROVAL_NOT_FOUND));
    }
}
