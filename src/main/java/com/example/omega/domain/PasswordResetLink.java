package com.example.omega.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PasswordResetLink extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(generator = "password_reset_link_sequence_generator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "password_reset_link_sequence_generator", initialValue = 1000, allocationSize = 1)
    private Long id;

    @Column
    private String token;

    @Column
    private Instant expirationTime;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
}