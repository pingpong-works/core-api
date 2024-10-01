package com.core.type.entity;

import com.core.document.entity.Document;
import com.core.template.entity.DocumentTemplate;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class DocumentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String type; //문서 유형이름
    //기안서, 연차/휴가 신청서, 특근신청서, 출장신청서, 업무보고서, 경비처리 신청서, 사내 제안서등

    @OneToMany(mappedBy = "documentType")
    private List<Document> documents; //문서에 해당하는 템플릿 (하나만)

    @OneToOne
    @JoinColumn(name = "template_id")
    private DocumentTemplate documentTemplate;

}
