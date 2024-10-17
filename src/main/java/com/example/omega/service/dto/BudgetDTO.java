package com.example.omega.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class BudgetDTO extends AbstractAuditingDTO {

    private Long id;

    @NotNull
    private BigDecimal budget;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private Long userId;
}
