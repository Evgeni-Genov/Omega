package com.example.omega.domain;

import com.example.omega.domain.enumeration.AccountStatus;
import com.example.omega.domain.enumeration.Roles;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @Column
    private String address;

    @Column
    private String townOfBirth;

    @Column
    private String countryOfBirth;

    @Column
    @OneToMany
    private List<AccountBalance> accountBalances;

    @Column
    @OneToMany(mappedBy = "sender")
    private List<Transaction> outgoingTransactions;

    @Column
    @OneToMany(mappedBy = "recipient")
    private List<Transaction> incomingTransactions;

    @Column
    private List<Roles> roles;

    @Column
    private boolean twoFactorAuthentication;
}
