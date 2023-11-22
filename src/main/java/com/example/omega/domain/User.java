package com.example.omega.domain;

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
    @GeneratedValue(generator = "user_sequence_generator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "user_sequence_generator", initialValue = 1000, allocationSize = 1)
    private Long id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String nameTag;

    @Column
    private String phoneNumber;

    @Column
    private Boolean locked;

    @Column
    private Boolean enabled;

    @Column
    private String address;

    @Column
    private String townOfBirth;

    @Column
    private String countryOfBirth;

    @Column
    @OneToMany(mappedBy = "user")
    private List<AccountBalance> accountBalances;

    @Column
    @OneToMany(mappedBy = "sender")
    private List<Transaction> outgoingTransactions;

    @Column
    @OneToMany(mappedBy = "recipient")
    private List<Transaction> incomingTransactions;

    @Column
    @Enumerated(EnumType.STRING)
    private Roles role;

    @Column
    private Boolean twoFactorAuthentication;

//    @Column
//    @JoinColumn(unique = true)
//    private UserDetailsImpl userDetails;

}
