package com.example.omega.domain;

import com.example.omega.domain.enumeration.Currency;
import com.example.omega.domain.enumeration.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column
    private User sender;

    @Column
    private User recipeint;

    @Column
    private BigDecimal amount;

    @Column
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;
}
