package com.example.omega.service;

import com.example.omega.domain.AccountBalance;
import com.example.omega.domain.enumeration.Currency;
import com.example.omega.domain.enumeration.TransactionStatus;
import com.example.omega.domain.enumeration.TransactionType;
import com.example.omega.mapper.TransactionMapper;
import com.example.omega.mapper.UserMapper;
import com.example.omega.repository.TransactionRepository;
import com.example.omega.repository.UserRepository;
import com.example.omega.service.dto.CreditCardDTO;
import com.example.omega.service.dto.TransactionDTO;
import com.example.omega.service.dto.TransactionSummaryDTO;
import com.example.omega.service.exception.BadRequestException;
import com.example.omega.service.util.TransactionServiceUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionService {

    private final UserService userService;
    private final BudgetService budgetService;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionServiceUtil transactionServiceUtil;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

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
     * Processes a money transfer transaction from one user to another.
     * This method handles the entire lifecycle of a money transfer transaction, including validation of funds,
     * updating account balances, and setting the transaction status. It ensures that the sender has sufficient funds,
     * respects the sender's budgeting constraints, and updates both the sender's and recipient's account balances.
     * If any step fails, appropriate exceptions are thrown, and the transaction is marked as failed.
     *
     * @param transactionDTO The data transfer object containing the transaction details.
     * @return A TransactionDTO representing the successfully completed transaction.
     * @throws BadRequestException if the sender does not have sufficient funds or if the transaction amount exceeds the sender's budget.
     */
    @Transactional
    public TransactionDTO sendFunds(TransactionDTO transactionDTO) {
        log.debug("Sending Money to user with nameTag: {} from user with id: {}", transactionDTO.getRecipientNameTag(), transactionDTO.getSenderId());
        var sender = userService.getUserById(transactionDTO.getSenderId());
        var senderBudgetingFlag = sender.getIsBudgetingEnabled();
        var recipient = userService.getUserByNameTag(transactionDTO.getRecipientNameTag());

        transactionDTO.setRecipientId(recipient.getId());

        var senderBalance = transactionServiceUtil.findAccountBalance(sender.getId(), transactionDTO.getCurrency());
        var transferAmount = transactionDTO.getAmount();

        if (senderBalance.getBalance().compareTo(transferAmount) < 0) {
            transactionDTO.setTransactionStatus(TransactionStatus.FAILED);
            transactionDTO.setIsExpense(true);
            saveTransaction(transactionDTO);
            throw new BadRequestException("Transaction failed due to insufficient funds");
        }

        if (Boolean.TRUE.equals(senderBudgetingFlag)) {
            var currentBudget = budgetService.getCurrentBudget(sender.getId());
            if (transferAmount.compareTo(currentBudget.getBudget()) > 0) {
                throw new BadRequestException("Transaction amount exceeds the budget");
            }
        }

        transactionDTO.setTransactionStatus(TransactionStatus.PROCESSING);
        transactionDTO.setIsExpense(true);
        var inProgressTransactionDTO = saveTransaction(transactionDTO);
        log.debug("Transaction in progress: {}", inProgressTransactionDTO);

        senderBalance.setBalance(senderBalance.getBalance().subtract(transferAmount));
        transactionServiceUtil.updateAccountBalance(senderBalance);

        var recipientBalance = transactionServiceUtil.findAccountBalance(recipient.getId(), transactionDTO.getCurrency());
        recipientBalance.setBalance(recipientBalance.getBalance().add(transferAmount));
        transactionServiceUtil.updateAccountBalance(recipientBalance);

        transactionDTO.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        var successfulTransactionDTO = saveTransaction(transactionDTO);
        log.debug("Transaction successful: {}", successfulTransactionDTO);

        transactionRepository.delete(transactionMapper.toEntity(inProgressTransactionDTO));

        return successfulTransactionDTO;
    }

    /**
     * Adds funds to a user's account balance based on the provided credit card details.
     * If the user is newly created and doesn't have an AccountBalance, he will have an AccountBalance created.
     * This method validates the credit card details and simulates a bank call to ensure the transaction can proceed.
     * If the credit card details are valid and the simulated bank call is successful, the funds are added to the user's account balance.
     * The transaction is then saved and returned as a {@link TransactionDTO} with a status of SUCCESSFUL.
     *
     * @param creditCardDTO The {@link CreditCardDTO} containing the credit card details and the amount to add.
     * @return A {@link TransactionDTO} representing the successfully completed transaction.
     * @throws BadRequestException if the credit card details are invalid or if the simulated bank call indicates insufficient funds.
     */
    @Transactional
    public TransactionDTO addFunds(CreditCardDTO creditCardDTO) {
        log.debug("Adding funds for user with ID: {}", creditCardDTO.getUserId());

        if (!transactionServiceUtil.isValidCardDetails(creditCardDTO)) {
            throw new BadRequestException("Invalid credit card details");
        }

        if (!transactionServiceUtil.simulateBankCall()) {
            throw new BadRequestException("Insufficient funds in the bank");
        }

        var transactionDTO = transactionMapper.toTransactionDTO(creditCardDTO);
        AccountBalance accountBalance;
        try {
            accountBalance = transactionServiceUtil.findAccountBalance(transactionDTO.getSenderId(), transactionDTO.getCurrency());
            accountBalance.setBalance(accountBalance.getBalance().add(transactionDTO.getAmount()));
        } catch (BadRequestException e) {
            accountBalance = new AccountBalance();
            accountBalance.setUser(userMapper.toEntity(userService.getUserById(transactionDTO.getSenderId())));
            accountBalance.setCurrency(transactionDTO.getCurrency());
            accountBalance.setBalance(transactionDTO.getAmount());
        }
        transactionServiceUtil.updateAccountBalance(accountBalance);

        transactionDTO.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        return saveTransaction(transactionDTO);
    }

    @Transactional
    public TransactionDTO requestFunds(TransactionDTO transactionDTO) {
        log.debug("Requesting funds from user with nameTag: {} to user with ID: {}", transactionDTO.getRecipientNameTag(), transactionDTO.getSenderId());

        var recipient = userRepository.findByNameTag(transactionDTO.getRecipientNameTag())
                .orElseThrow(() -> new BadRequestException("User not found with nameTag: " + transactionDTO.getRecipientNameTag()));

        if (transactionDTO.getSenderId().equals(recipient.getId())) {
            throw new BadRequestException("Sender and recipient cannot be the same user");
        }

        transactionDTO.setRecipientId(recipient.getId());
        transactionDTO.setTransactionType(TransactionType.TRANSFER);
        transactionDTO.setTransactionStatus(TransactionStatus.PENDING);
        transactionDTO.setIsExpense(false);

        if (transactionDTO.getCurrency() == null) {
            transactionDTO.setCurrency(Currency.USD);
        }

        if (transactionDTO.getAmount() == null || transactionDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount for fund request");
        }

        return saveTransaction(transactionDTO);
    }

    @Transactional
    public TransactionDTO cancelRequestedFunds(Long transactionId, Long currentUserId){
        log.debug("User with ID:{} is cancelling the requested funds", currentUserId);

        var transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new BadRequestException("Transaction not found!"));

        if(!transaction.getRecipient().getId().equals(currentUserId)){
            throw new BadRequestException("You can cancel only Transaction where the Funds are requested FROM YOU!");
        }

        if (transaction.getTransactionStatus() != TransactionStatus.PENDING) {
            throw new BadRequestException("Only pending transactions can be cancelled!");
        }

        transaction.setAmount(BigDecimal.ZERO);
        transaction.setTransactionStatus(TransactionStatus.CANCELLED);
        transaction.setDescription("This Transaction was cancelled!");

        var savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDTO(savedTransaction);
    }

    /**
     * Retrieves all transactions for a given user, either as the recipient or the sender.
     * This method fetches a paginated list of transactions where the user is involved,
     * either by sending or receiving funds, and converts them to TransactionDTO objects.
     *
     * @param pageable the pagination information.
     * @param userId   the ID of the user whose transactions are to be retrieved.
     * @return a page of TransactionDTO objects representing the user's transactions.
     */
    public Page<TransactionDTO> getAllTransactionsForAUser(Pageable pageable, Long userId) {
        return transactionRepository
                .findAllByRecipientAndSender(userId, pageable)
                .map(transactionMapper::toDTO);
    }

    /**
     * Retrieves all transactions between two specified users and converts them to DTOs.
     *
     * @param userId      the ID of the first user
     * @param otherUserId the ID of the second user
     * @return a Page of TransactionSummaryDTO objects representing the transactions between the two users
     */
    public Page<TransactionSummaryDTO> getAllTransactionBetweenTwoUsers(Pageable pageable, Long userId, Long otherUserId) {
        var transactions = transactionRepository.findByUserIdAndOtherUserId(userId, otherUserId, pageable);

        return transactions.map(transactionMapper::toTransactionSummaryDTO);
    }

    /**
     * Get Transaction by ID.
     *
     * @param id the id of the supposed Transaction.
     * @return the TransactionDTO if it exists.
     */
    public TransactionDTO getTransactionById(Long id){
        var fetchedTransaction = transactionRepository.findById(id).orElseThrow(() -> new BadRequestException("Transaction not found!"));
        return transactionMapper.toDTO(fetchedTransaction);
    }

    public List<TransactionDTO> getPendingFundRequestsForUser(Long userId) {
        var pendingFundRequests = transactionRepository.findPendingFundRequestsForUser(userId);
        return pendingFundRequests.stream()
                .map(transactionMapper::toDTO)
                .toList();
    }
}
