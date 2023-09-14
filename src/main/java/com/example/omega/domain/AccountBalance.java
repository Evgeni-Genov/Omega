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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
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
