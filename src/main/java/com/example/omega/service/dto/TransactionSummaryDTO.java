package com.example.omega.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSummaryDTO extends AbstractAuditingDTO {

    private BigDecimal amount;
    private String currency;
    private String description;
    private Long senderId;
    private Long recipientId;
    private Boolean isFriend;
}
