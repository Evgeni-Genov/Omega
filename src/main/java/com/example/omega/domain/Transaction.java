package com.example.omega.domain;

import com.example.omega.domain.enumeration.Currency;
import com.example.omega.domain.enumeration.TransactionStatus;
import com.example.omega.domain.enumeration.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.javers.core.metamodel.annotation.DiffIgnore;

import java.math.BigDecimal;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Transaction extends AbstractAuditingEntity{

    @Id
    @GeneratedValue(generator = "transaction_sequence_generator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "transaction_sequence_generator", initialValue = 1000, allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    @DiffIgnore
    private User sender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipient_id")
    @DiffIgnore
    private User recipient;

    @Column
    private BigDecimal amount;

    @Column
    @DiffIgnore
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column
    @DiffIgnore
    private Boolean isExpense;

}
