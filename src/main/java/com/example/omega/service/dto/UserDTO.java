package com.example.omega.service.dto;

import com.example.omega.domain.AccountBalance;
import com.example.omega.domain.Transaction;
import com.example.omega.domain.enumeration.AccountStatus;
import com.example.omega.domain.enumeration.Roles;
import jakarta.persistence.Column;
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

    private String userName;

    private String email;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    private List<AccountBalance> accountBalances;

    private List<Transaction> outgoingTransactions;

    private List<Transaction> incomingTransactions;

    private List<Roles> roles;

    private boolean twoFactorAuthentication;
}
