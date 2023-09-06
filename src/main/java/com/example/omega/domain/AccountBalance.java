package com.example.omega.domain;

import com.example.omega.domain.enumeration.Currency;
import jakarta.persistence.*;
import lombok.*;


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

    @Column
    @OneToOne
    private User user;

    @Column
    @Enumerated(EnumType.STRING)
    private Currency currency;
}
