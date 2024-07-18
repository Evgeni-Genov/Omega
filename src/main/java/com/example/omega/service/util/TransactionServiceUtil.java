package com.example.omega.service.util;

import com.example.omega.domain.AccountBalance;
import com.example.omega.domain.Budget;
import com.example.omega.domain.Transaction;
import com.example.omega.domain.enumeration.Currency;
import com.example.omega.repository.AccountBalanceRepository;
import com.example.omega.repository.TransactionRepository;
import com.example.omega.service.UserService;
import com.example.omega.service.dto.CreditCardDTO;
import com.example.omega.service.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Utility class for transaction-related operations in the application.
 * This class provides methods for validating credit card details, simulating bank calls,
 * calculating the total amount spent by a user, updating account balances, and finding account balances
 * for a specific user and currency. It leverages the Luhn algorithm for credit card number validation
 * and checks credit card expiry dates to ensure they are not past. Additionally, it offers a method
 * to simulate bank approval for transactions, aiding in the development and testing phases.
 */
@Component
@Slf4j
@AllArgsConstructor
public class TransactionServiceUtil {

    private final TransactionRepository transactionRepository;
    private final AccountBalanceRepository accountBalanceRepository;
    private final UserService userService;

    /**
     * Validates the credit card details including card number and expiry date.
     *
     * @param creditCardDTO The credit card data transfer object containing the card details.
     * @return true if both the card number and expiry date are valid, false otherwise.
     */
    public boolean isValidCardDetails(CreditCardDTO creditCardDTO) {
        return isValidCardNumber(creditCardDTO.getCardNumber())
                && isValidExpiryDate(creditCardDTO.getExpiryDate());
    }

    /**
     * Validates the credit card number using the Luhn algorithm.
     *
     * @param cardNumber The credit card number as a String.
     * @return true if the card number is valid according to the Luhn algorithm, false otherwise.
     */
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

    /**
     * Validates the credit card expiry date. The expiry date should not be before the current month.
     *
     * @param expiryDate The expiry date of the credit card in the format "yyyy-MM".
     * @return true if the expiry date is not before the current month, false otherwise.
     */
    private boolean isValidExpiryDate(String expiryDate) {
        try {
            var expiry = YearMonth.parse(expiryDate.substring(0, 7), DateTimeFormatter.ofPattern("yyyy-MM"));
            var currentMonth = YearMonth.now();
            return !expiry.isBefore(currentMonth);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Simulates a bank call to check if the transaction can be approved.
     *
     * @return true with a 90% chance to simulate a successful bank transaction approval, false otherwise.
     */
    public boolean simulateBankCall() {
        return Math.random() > 0.1; // 90% chance that the bank approves the transaction
    }

    public BigDecimal calculateTotalSpentInTimeRange(Long userId, Budget budget) {
        log.debug("Calculating total amount spent by user with ID: {}", userId);

        var startDateTime = budget.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant();
        var endDateTime = budget.getEndDate().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        var transactions = transactionRepository.findByCreatedDateBetweenAndSenderId(startDateTime, endDateTime, userId);

        return transactions
                .stream()
                .filter(Transaction::getIsExpense)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Updates the account balance in the database.
     *
     * @param accountBalance The account balance to be updated.
     * @return The updated account balance.
     */
    public AccountBalance updateAccountBalance(AccountBalance accountBalance) {
        var updatedBalance = accountBalanceRepository.save(accountBalance);
        log.debug("Account balance updated: {}", updatedBalance);
        return updatedBalance;
    }

    /**
     * Finds the account balance for the specified user and currency.
     *
     * @param userId   The ID representing the user.
     * @param currency The currency for which the account balance needs to be found.
     * @return The account balance for the specified user and currency.
     * @throws BadRequestException if the account balance is not found.
     */
    public AccountBalance findAccountBalance(Long userId, Currency currency) {
        var userDTO = userService.getUserById(userId);
        return userDTO
                .getAccountBalances()
                .stream()
                .filter(balance -> balance.getCurrency() == currency)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Account balance not found"));
    }

    /**
     * Calculates the total amount of funds added to and spent from a user's account within a specified time range.
     * This method processes a list of transactions, segregating them into added funds and spent funds based on the transaction type.
     * It then sums up the amounts for each category to provide a comprehensive overview of the user's financial activity.
     *
     * @param transactions A list of {@link Transaction} objects representing the user's transactions within the specified time range.
     * @param userId       The ID of the user for whom the calculation is being performed.
     * @return An {@link ImmutablePair} containing two {@link BigDecimal} values: the first represents the total added funds,
     * and the second represents the total spent funds.
     */
    public ImmutablePair<BigDecimal, BigDecimal> calculateTotalAddedFundsAndTotalSpentInTimeRange(List<Transaction> transactions, Long userId) {
        log.debug("Calculating total amount added and spent by user with ID: {}", userId);
        var totalAddedFunds = transactions
                .stream()
                .filter(transaction -> !transaction.getIsExpense())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var totalSpent = transactions
                .stream()
                .filter(Transaction::getIsExpense)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ImmutablePair<>(totalAddedFunds, totalSpent);
    }
}
