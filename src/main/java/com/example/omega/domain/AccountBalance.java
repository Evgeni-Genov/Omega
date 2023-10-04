package com.example.omega.domain;

import com.example.omega.domain.enumeration.Currency;
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
public class AccountBalance {

    @Id
    @GeneratedValue(generator = "account_balance_sequence_generator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "account_balance_sequence_generator", initialValue = 1000, allocationSize = 1)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    @Column
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column
    private BigDecimal balance;
}
