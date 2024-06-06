package com.example.omega.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AddFundsDTO {

    @NotNull
    private Long userId;

    @NotNull
    private String cardNumber;

    @NotNull
    private String cardOwner;

    @NotNull
    private String expiryDate;

    @NotNull
    private String securityCode;

    @NotNull
    private BigDecimal amount;
}
