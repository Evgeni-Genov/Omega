package com.example.omega.service.dto;

import com.example.omega.domain.enumeration.Currency;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountBalanceDTO extends AbstractAuditingDTO {

    private Long id;

    @NotNull
    private Long userId;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private BigDecimal balance;
}
