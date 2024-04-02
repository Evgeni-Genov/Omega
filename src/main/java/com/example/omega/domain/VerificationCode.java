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
public class VerificationCode extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(generator = "verification_code_sequence_generator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "verification_code_sequence_generator", initialValue = 1000, allocationSize = 1)
    private Long id;

    @Column
    private String code;

    @Column
    private Instant expirationTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
