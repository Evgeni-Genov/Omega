package com.example.omega.service.dto;

import com.example.omega.service.util.StringNormalizationDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreditCardDTO {

    @NotNull
    private Long userId;

    @NotNull
    @Size(min = 16, max = 16)
    private String cardNumber;

    @NotBlank
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String cardOwner;

    @NotNull
    private String expiryDate;

    @NotNull
    @Size(min = 3, max = 3)
    @Pattern(regexp = "^[0-9]{3,4}$")
    private String securityCode;

    @NotNull
    @Positive
    private BigDecimal amount;
}
