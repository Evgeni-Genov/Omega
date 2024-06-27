package com.example.omega.domain;

import com.example.omega.domain.enumeration.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
    private String email;

    @Column
    private String password;

    @Column
    private String nameTag;

    @Column
    @Pattern(regexp = "^\\+\\d{1,3}\\d{1,14}(\\s\\d{1,13})?$", message = "Phone number must be a valid international number starting with '+'")
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
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<AccountBalance> accountBalances;

    @Column
    @OneToMany(mappedBy = "sender", fetch = FetchType.EAGER)
    private List<Transaction> outgoingTransactions;

    @Column
    @OneToMany(mappedBy = "recipient", fetch = FetchType.EAGER)
    private List<Transaction> incomingTransactions;

    @Column
    @Enumerated(EnumType.STRING)
    private Roles role;

    @Column
    private Boolean twoFactorAuthentication;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private VerificationCode verificationCode;

    @Column
    private String emailVerificationToken;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private PasswordResetLink passwordResetLink;

    @Column
    private String twoFactorSecret;

    @Column
    private String avatar;

//    @Column
//    @JoinColumn(unique = true)
//    private UserDetailsImpl userDetails;

    public void setVerificationCode(VerificationCode verificationCode) {
        this.verificationCode = verificationCode;
        verificationCode.setUser(this);
    }

    public void setPasswordResetLink(PasswordResetLink passwordResetLink) {
        this.passwordResetLink = passwordResetLink;
        passwordResetLink.setUser(this);
    }

}
