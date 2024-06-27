package com.example.omega.service;

import com.example.omega.mapper.AccountBalanceMapper;
import com.example.omega.repository.AccountBalanceRepository;
import com.example.omega.service.dto.AccountBalanceDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class AccountBalanceService {

    private final AccountBalanceRepository accountBalanceRepository;
    private final AccountBalanceMapper accountBalanceMapper;

    public AccountBalanceDTO create(AccountBalanceDTO accountBalanceDTO) {
        var accountBalance = accountBalanceMapper.toEntity(accountBalanceDTO);
        accountBalance = accountBalanceRepository.save(accountBalance);
        return accountBalanceMapper.toDTO(accountBalance);
    }

    public Optional<AccountBalanceDTO> partialUpdate(AccountBalanceDTO accountBalanceDTO) {
        return accountBalanceRepository.findById(accountBalanceDTO.getId())
                .map(existingAccountBalance -> {
                    if (accountBalanceDTO.getCurrency() != null) {
                        existingAccountBalance.setCurrency(accountBalanceDTO.getCurrency());
                    }
                    if (accountBalanceDTO.getBalance() != null) {
                        existingAccountBalance.setBalance(accountBalanceDTO.getBalance());
                    }
                    return existingAccountBalance;
                })
                .map(accountBalanceRepository::save)
                .map(accountBalanceMapper::toDTO);
    }

    public Page<AccountBalanceDTO> getAllForUser(Pageable pageable, Long userId) {
        return accountBalanceRepository.findAllByUserId(userId, pageable)
                .map(accountBalanceMapper::toDTO);
    }

    @Transactional
    public Optional<AccountBalanceDTO> getOne(Long id) {
        return accountBalanceRepository.findById(id)
                .map(accountBalanceMapper::toDTO);
    }

    public void deleteById(Long id) {
        accountBalanceRepository.deleteById(id);
    }
}