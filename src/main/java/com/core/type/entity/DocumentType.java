package com.core.type.entity;

import com.core.document.entity.Document;
import com.core.template.entity.DocumentTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class DocumentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String type;

    @OneToMany(mappedBy = "documentType")
    private List<Document> documents;

    @OneToOne
    @JoinColumn(name = "template_id")
    private DocumentTemplate documentTemplate;

}
