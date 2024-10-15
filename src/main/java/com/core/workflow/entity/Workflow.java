package com.core.workflow.entity;

import com.core.approval.entity.Approval;
import com.core.document.entity.Document;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Workflow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "workflow")
    private Document document;

    @Column
    private int currentStep = 1;

    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Approval> approvals = new ArrayList<>();

}
