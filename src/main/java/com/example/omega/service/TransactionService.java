package com.example.omega.service;

import com.example.omega.domain.AccountBalance;
import com.example.omega.domain.enumeration.Currency;
import com.example.omega.domain.enumeration.TransactionStatus;
import com.example.omega.mapper.TransactionMapper;
import com.example.omega.repository.AccountBalanceRepository;
import com.example.omega.repository.TransactionRepository;
import com.example.omega.service.dto.TransactionDTO;
import com.example.omega.service.dto.UserDTO;
import com.example.omega.service.exception.BadRequestException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionService {

    private final UserService userService;

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    private final AccountBalanceRepository accountBalanceRepository;

    /**
     * Save a transaction based on the provided TransactionDTO.
     *
     * @param transactionDTO The TransactionDTO containing transaction information.
     * @return A TransactionDTO representing the created transaction.
     */
    public TransactionDTO saveTransaction(TransactionDTO transactionDTO) {
        log.debug("Creating Transaction!");
        var transaction = transactionMapper.toEntity(transactionDTO);
        return transactionMapper.toDTO(transactionRepository.saveAndFlush(transaction));
    }

    /**
     * Sends money from one user to another.
     *
     * @param transactionDTO The DTO representing the transaction details.
     * @return The DTO representing the updated transaction details.
     */
    @Transactional
    public TransactionDTO sendMoney(TransactionDTO transactionDTO) {
        log.debug("Sending Money to user with id: {} from user with id: {}", transactionDTO.getRecipientId(), transactionDTO.getSenderId());

        var sender = userService.getUserById(transactionDTO.getSenderId());
        var recipient = userService.getUserById(transactionDTO.getRecipientId());

        var senderBalance = findAccountBalance(sender, transactionDTO.getCurrency());
        var transferAmount = transactionDTO.getAmount();

        if (senderBalance.getBalance().compareTo(transferAmount) < 0) {
            transactionDTO.setTransactionStatus(TransactionStatus.FAILED);
            var failedTransactionDTO = saveTransaction(transactionDTO);
            log.debug("Transaction failed due to insufficient funds: {}", failedTransactionDTO);
            return failedTransactionDTO;
        }

        transactionDTO.setTransactionStatus(TransactionStatus.PROCESSING);
        var inProgressTransactionDTO = saveTransaction(transactionDTO);
        log.debug("Transaction in progress: {}", inProgressTransactionDTO);

        senderBalance.setBalance(senderBalance.getBalance().subtract(transferAmount));

        var updatedSenderBalance = updateAccountBalance(senderBalance);
        log.debug("Sender balance updated: {}", updatedSenderBalance);

        var recipientBalance = findAccountBalance(recipient, transactionDTO.getCurrency());
        recipientBalance.setBalance(recipientBalance.getBalance().add(transferAmount));

        var updatedRecipientBalance = updateAccountBalance(recipientBalance);
        log.debug("Recipient balance updated: {}", updatedRecipientBalance);

        transactionDTO.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        var successfulTransactionDTO = saveTransaction(transactionDTO);
        log.debug("Transaction successful: {}", successfulTransactionDTO);

        return successfulTransactionDTO;
    }

    public Page<TransactionDTO> getAllTransactionsForAUser(Pageable pageable, Long userId) {
        return transactionRepository.findAllByRecipientAndSender(userId, pageable)
                .map(transactionMapper::toDTO);
    }

    /**
     * Updates the account balance in the database.
     *
     * @param accountBalance The account balance to be updated.
     * @return The updated account balance.
     */
    private AccountBalance updateAccountBalance(AccountBalance accountBalance) {
        var updatedBalance = accountBalanceRepository.save(accountBalance);
        log.debug("Account balance updated: {}", updatedBalance);
        return updatedBalance;
    }

    /**
     * Finds the account balance for the specified user and currency.
     *
     * @param userDTO  The DTO representing the user.
     * @param currency The currency for which the account balance needs to be found.
     * @return The account balance for the specified user and currency.
     * @throws BadRequestException if the account balance is not found.
     */
    private AccountBalance findAccountBalance(UserDTO userDTO, Currency currency) {
        return userDTO
                .getAccountBalances()
                .stream()
                .filter(balance -> balance.getCurrency() == currency)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Account balance not found"));
    }
}
