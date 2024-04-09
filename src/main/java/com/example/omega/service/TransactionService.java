package com.example.omega.service;

import com.example.omega.domain.AccountBalance;
import com.example.omega.domain.TransactionStateHistory;
import com.example.omega.domain.enumeration.Currency;
import com.example.omega.domain.enumeration.TransactionStatus;
import com.example.omega.mapper.TransactionMapper;
import com.example.omega.repository.TransactionRepository;
import com.example.omega.repository.TransactionStateHistoryRepository;
import com.example.omega.service.dto.TransactionDTO;
import com.example.omega.service.dto.UserDTO;
import com.example.omega.service.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionService {

    //TODO: check Activities in ARTool: history

    private final UserService userService;

    private final TransactionRepository transactionRepository;

    private final TransactionStateHistoryRepository transactionStateHistoryRepository;

    private final TransactionMapper transactionMapper;


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
        log.debug("Sending Money to user with id: {} from user with id: {}", transactionDTO.getRecipientId(), transactionDTO.getSenderId());
        var sender = userService.getUserById(transactionDTO.getSenderId());
        var recipient = userService.getUserById(transactionDTO.getRecipientId());

        var senderBalance = findAccountBalance(sender, transactionDTO.getCurrency());
        var transferAmount = transactionDTO.getAmount();

        if (senderBalance.getBalance().compareTo(transferAmount) < 0) {
            throw new BadRequestException("Insufficient funds");
        }

        senderBalance.setBalance(senderBalance.getBalance().subtract(transferAmount));

        transactionDTO.setTransactionStatus(TransactionStatus.PENDING);

        var savedTransactionDTO = saveTransaction(transactionDTO);

        var transactionStateHistory = TransactionStateHistory
                .builder()
                .previousState(TransactionStatus.PENDING)
                .newState(TransactionStatus.PENDING)
                .transaction(transactionMapper.toEntity(savedTransactionDTO))
                .build();

        transactionStateHistoryRepository.save(transactionStateHistory);

        var recipientBalance = findAccountBalance(recipient, transactionDTO.getCurrency());
        recipientBalance.setBalance(recipientBalance.getBalance().add(transferAmount));

        return transactionDTO;
    }

    private AccountBalance findAccountBalance(UserDTO userDTO, Currency currency) {
        return userDTO
                .getAccountBalances()
                .stream()
                .filter(balance -> balance.getCurrency() == currency)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Account balance not found"));
    }
}
