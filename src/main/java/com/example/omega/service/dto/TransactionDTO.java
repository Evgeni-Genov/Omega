package com.example.omega.service.dto;

import com.example.omega.domain.enumeration.Currency;
import com.example.omega.domain.enumeration.TransactionStatus;
import com.example.omega.domain.enumeration.TransactionType;
import com.example.omega.service.util.StringNormalizationDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionDTO extends AbstractAuditingDTO {

    private Long id;

    @NotNull
    private Long senderId;

    @NotNull
    private Long recipientId;

    @Positive
    @NotNull
    private BigDecimal amount;

    @Size(max = 100)
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String description;

    @Enumerated(EnumType.STRING)
    @Schema(type = "String", allowableValues = {"USD", "BGN", "EUR", "GBP", "JPY"}, description = "Currency")
    private Currency currency;

    private TransactionStatus transactionStatus;

    @Enumerated(EnumType.STRING)
    @Schema(type = "String", allowableValues = {"PURCHASE", "TRANSFER", "WITHDRAWAL"}, description = "Transaction Type")
    private TransactionType type;

}
