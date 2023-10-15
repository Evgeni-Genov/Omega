package com.example.omega.service.dto;

import com.example.omega.domain.AccountBalance;
import com.example.omega.domain.Transaction;
import com.example.omega.domain.enumeration.Roles;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {

    private Long id;

    private String username;

    private String email;

    private Boolean locked;

    private Boolean enabled;

    private List<AccountBalance> accountBalances;

    private List<Transaction> outgoingTransactions;

    private List<Transaction> incomingTransactions;

    @Enumerated(EnumType.STRING)
    private Roles role;

    private Boolean twoFactorAuthentication;
}
