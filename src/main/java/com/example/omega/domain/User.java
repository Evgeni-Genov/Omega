package com.example.omega.domain;

import com.example.omega.domain.enumeration.Roles;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User extends AbstractAuditingEntity implements UserDetails {

    @Id
    @GeneratedValue(generator = "user_sequence_generator",strategy = GenerationType.SEQUENCE)
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
    @OneToMany
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

    public User(String firstName, String lastName, String username, String email,
                String password, String nameTag, String phoneNumber, Boolean locked,
                Boolean enabled, String address, String townOfBirth, String countryOfBirth,
                List<AccountBalance> accountBalances, List<Transaction> outgoingTransactions,
                List<Transaction> incomingTransactions, Roles role, boolean twoFactorAuthentication) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.nameTag = nameTag;
        this.phoneNumber = phoneNumber;
        this.locked = locked;
        this.enabled = enabled;
        this.address = address;
        this.townOfBirth = townOfBirth;
        this.countryOfBirth = countryOfBirth;
        this.accountBalances = accountBalances;
        this.outgoingTransactions = outgoingTransactions;
        this.incomingTransactions = incomingTransactions;
        this.role = role;
        this.twoFactorAuthentication = twoFactorAuthentication;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities () {
        var authority =
                new SimpleGrantedAuthority(role.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
