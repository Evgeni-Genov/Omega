package com.example.omega.domain;

import com.example.omega.domain.enumeration.AccountStatus;
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
public class User extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String nameTag;

    @Column
    private String phoneNumber;

    @Column
    @OneToOne
    private AccountBalance balance;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
}
