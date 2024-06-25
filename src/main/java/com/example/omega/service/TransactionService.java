package com.example.omega.service;

import com.example.omega.domain.AccountBalance;
import com.example.omega.domain.enumeration.Currency;
import com.example.omega.domain.enumeration.TransactionStatus;
import com.example.omega.mapper.TransactionMapper;
import com.example.omega.repository.AccountBalanceRepository;
import com.example.omega.repository.TransactionRepository;
import com.example.omega.service.dto.CreditCardDTO;
import com.example.omega.service.dto.TransactionDTO;
import com.example.omega.service.exception.BadRequestException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

    @Transactional
    public TransactionDTO sendMoney(TransactionDTO transactionDTO) {
        log.debug("Sending Money to user with nameTag: {} from user with id: {}", transactionDTO.getUserNameTag(), transactionDTO.getSenderId());

        var sender = userService.getUserById(transactionDTO.getSenderId());
        var recipient = userService.getUserByNameTag(transactionDTO.getUserNameTag());

        var senderBalance = findAccountBalance(sender.getId(), transactionDTO.getCurrency());
        var transferAmount = transactionDTO.getAmount();

        if (senderBalance.getBalance().compareTo(transferAmount) < 0) {
            transactionDTO.setTransactionStatus(TransactionStatus.FAILED);
            transactionDTO.setIsExpense(true);
            var failedTransactionDTO = saveTransaction(transactionDTO);
            log.debug("Transaction failed due to insufficient funds: {}", failedTransactionDTO);
            return failedTransactionDTO;
        }

        transactionDTO.setRecipientId(recipient.getId());
        transactionDTO.setTransactionStatus(TransactionStatus.PROCESSING);
        transactionDTO.setIsExpense(true);
        var inProgressTransactionDTO = saveTransaction(transactionDTO);
        log.debug("Transaction in progress: {}", inProgressTransactionDTO);

        senderBalance.setBalance(senderBalance.getBalance().subtract(transferAmount));
        updateAccountBalance(senderBalance);

        var recipientBalance = findAccountBalance(recipient.getId(), transactionDTO.getCurrency());
        recipientBalance.setBalance(recipientBalance.getBalance().add(transferAmount));
        updateAccountBalance(recipientBalance);

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
     * @param userId  The ID representing the user.
     * @param currency The currency for which the account balance needs to be found.
     * @return The account balance for the specified user and currency.
     * @throws BadRequestException if the account balance is not found.
     */
    private AccountBalance findAccountBalance(Long userId, Currency currency) {
        var userDTO = userService.getUserById(userId);
        return userDTO
                .getAccountBalances()
                .stream()
                .filter(balance -> balance.getCurrency() == currency)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Account balance not found"));
    }

    @Transactional
    public TransactionDTO addFunds(CreditCardDTO creditCardDTO) {
        log.debug("Adding funds for user with ID: {}", creditCardDTO.getUserId());

        if (!isValidCardDetails(creditCardDTO)) {
            throw new BadRequestException("Invalid credit card details");
        }

        if (!simulateBankCall()) {
            throw new BadRequestException("Insufficient funds in the bank");
        }

        var transactionDTO = transactionMapper.toTransactionDTO(creditCardDTO);
        var accountBalance = findAccountBalance(transactionDTO.getSenderId(), transactionDTO.getCurrency());

        accountBalance.setBalance(accountBalance.getBalance().add(transactionDTO.getAmount()));
        updateAccountBalance(accountBalance);

        transactionDTO.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        return saveTransaction(transactionDTO);
    }

    private boolean isValidCardDetails(CreditCardDTO creditCardDTO) {
        return isValidCardNumber(creditCardDTO.getCardNumber())
                && isValidExpiryDate(creditCardDTO.getExpiryDate());
    }

    private boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null) {
            return false;
        }

        cardNumber = cardNumber.replaceAll("\\s", "");

        if (cardNumber.length() != 16 || !cardNumber.matches("\\d+")) {
            return false;
        }

        var sum = 0;
        var alternate = false;
        for (var i = cardNumber.length() - 1; i >= 0; i--) {
            var n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            sum += n;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }


    private boolean isValidExpiryDate(String expiryDate) {
        try {
            var expiry = YearMonth.parse(expiryDate.substring(0, 7), DateTimeFormatter.ofPattern("yyyy-MM"));
            var currentMonth = YearMonth.now();
            return !expiry.isBefore(currentMonth);
        } catch (DateTimeParseException e) {
            return false;
        }
    }


    private boolean simulateBankCall() {
        return Math.random() > 0.1; // 90% chance that the bank approves the transaction
    }
}
