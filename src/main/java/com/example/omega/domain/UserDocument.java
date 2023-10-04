package com.example.omega.domain;

import jakarta.persistence.*;
import lombok.*;



@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDocument extends AbstractAuditingEntity{

    @Id
    @GeneratedValue(generator = "user_document_sequence_generator",strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "user_document_sequence_generator", initialValue = 1000, allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String documentType;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] documentData;
}
