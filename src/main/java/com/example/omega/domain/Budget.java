package com.example.omega.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Budget extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(generator = "budget_sequence_generator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "budget_sequence_generator", initialValue = 1000, allocationSize = 1)
    private Long id;

    @Column
    private BigDecimal budget;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
