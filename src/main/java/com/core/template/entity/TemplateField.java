package com.core.template.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor

public class TemplateField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private DocumentTemplate documentTemplate;

    @Column(nullable = false)
    private String fieldName;

    @Column(nullable = false)
    private String fieldType;
}
