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

    /**
     * Service method to create a new account balance.
     *
     * @param accountBalanceDTO The DTO representing the account balance to create.
     * @return The DTO of the newly created account balance.
     */
    public AccountBalanceDTO create(AccountBalanceDTO accountBalanceDTO) {
        var accountBalance = accountBalanceMapper.toEntity(accountBalanceDTO);
        accountBalance = accountBalanceRepository.save(accountBalance);
        return accountBalanceMapper.toDTO(accountBalance);
    }

    /**
     * Service method to partially update an existing account balance.
     * It updates only the non-null fields of the provided AccountBalanceDTO.
     *
     * @param accountBalanceDTO The DTO containing the fields to update.
     * @return An Optional containing the updated AccountBalanceDTO, or an empty Optional if the account balance was not found.
     */
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

    /**
     * Service method to retrieve a paginated list of all account balances for a specific user.
     *
     * @param pageable The pagination information.
     * @param userId The ID of the user for whom to retrieve account balances.
     * @return A page of AccountBalanceDTO objects representing the user's account balances.
     */
    public Page<AccountBalanceDTO> getAllForUser(Pageable pageable, Long userId) {
        return accountBalanceRepository.findAllByUserId(userId, pageable)
                .map(accountBalanceMapper::toDTO);
    }

    /**
     * Service method to retrieve a single account balance by its ID.
     * The operation is transactional.
     *
     * @param id The ID of the account balance to retrieve.
     * @return An Optional containing the AccountBalanceDTO if found, or an empty Optional if not found.
     */
    @Transactional
    public Optional<AccountBalanceDTO> getOne(Long id) {
        return accountBalanceRepository.findById(id)
                .map(accountBalanceMapper::toDTO);
    }
    /**
     * Service method to delete an account balance by its ID.
     *
     * @param id The ID of the account balance to delete.
     */
    public void deleteById(Long id) {
        accountBalanceRepository.deleteById(id);
    }

}

