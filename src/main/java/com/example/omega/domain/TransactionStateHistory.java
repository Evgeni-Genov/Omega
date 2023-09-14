package com.example.omega.domain;

import com.example.omega.domain.enumeration.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TransactionStateHistory extends AbstractAuditingEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionStatus previousState;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionStatus newState;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;
}
