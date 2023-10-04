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
    @GeneratedValue(generator = "transaction_state_history_sequence_generator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "transaction_state_history_sequence_generator", initialValue = 1000, allocationSize = 1)
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
