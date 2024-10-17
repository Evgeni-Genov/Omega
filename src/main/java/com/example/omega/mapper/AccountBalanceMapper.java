package com.example.omega.mapper;

import com.example.omega.domain.AccountBalance;
import com.example.omega.service.dto.AccountBalanceDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountBalanceMapper {

    AccountBalanceDTO toDTO(AccountBalance accountBalance);

    AccountBalance toEntity(AccountBalanceDTO accountBalanceDTO);
}
