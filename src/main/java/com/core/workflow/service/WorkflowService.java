package com.core.workflow.service;

import com.core.approval.repository.ApprovalRepository;
import com.core.exception.BusinessLogicException;
import com.core.exception.ExceptionCode;
import com.core.workflow.entity.Workflow;
import com.core.workflow.repository.WorkflowRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
public class WorkflowService {
    private final WorkflowRepository workflowRepository;
    private final ApprovalRepository approvalRepository;

    public WorkflowService(WorkflowRepository workflowRepository, ApprovalRepository approvalRepository) {
        this.workflowRepository = workflowRepository;
        this.approvalRepository = approvalRepository;
    }

    public Workflow createWorkflow(Workflow workflow) {
        //approval : 한 workflow 내에 employeeId 중복 안되게하기
        Set<Long> employeeIds = new HashSet<>();

        workflow.getApprovals().forEach(approval -> {
            if (!employeeIds.add(approval.getEmployeeId())) {
                System.out.println("승인자 중복 : " + approval.getEmployeeId());
                throw new BusinessLogicException(ExceptionCode.EMPLOYEE_IN_APPROVAL_IS_DUPLICATE);
            }
        });

        return workflowRepository.save(workflow);
    }

    public Workflow updateWorkflow(Workflow workflow) {
        return workflowRepository.save(workflow);
    }

    public void deleteWorkflow(Long workflowId) {

        workflowRepository.delete(findVerifiedWorkflow(workflowId));
    }


    public Workflow findWorkflow(Long workflowId) {
        return findVerifiedWorkflow(workflowId);
    }

    private Workflow findVerifiedWorkflow (Long workflowId) {
        return workflowRepository.findById(workflowId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.WORKFLOW_NOT_FOUND));
    }
}
