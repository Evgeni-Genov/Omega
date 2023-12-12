package com.example.omega.service;

import com.example.omega.domain.AccountBalance;
import com.example.omega.domain.TransactionStateHistory;
import com.example.omega.domain.enumeration.Currency;
import com.example.omega.domain.enumeration.TransactionStatus;
import com.example.omega.mapper.TransactionMapper;
import com.example.omega.mapper.UserMapper;
import com.example.omega.repository.TransactionRepository;
import com.example.omega.repository.TransactionStateHistoryRepository;
import com.example.omega.service.dto.TransactionDTO;
import com.example.omega.service.dto.UserDTO;
import com.example.omega.service.exception.HttpBadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TransactionService {

    //TODO: check Activities in ARTool: history

    private final UserService userService;

    private final UserMapper userMapper;

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
        var transaction = transactionMapper.toEntity(transactionDTO);
//        transaction.setSender(currentUser);
        return transactionMapper.toDTO(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionDTO sendMoney(TransactionDTO transactionDTO) {
        var sender = userService.getUserById(transactionDTO.getSenderId());
        var recipient = userService.getUserById(transactionDTO.getRecipientId());

        var senderBalance = findAccountBalance(sender, transactionDTO.getCurrency());
        var transferAmount = transactionDTO.getAmount();

        if (senderBalance.getBalance().compareTo(transferAmount) < 0) {
            throw new HttpBadRequestException("Insufficient funds");

        }

        senderBalance.setBalance(senderBalance.getBalance().subtract(transferAmount));

        var transactionStateHistory = TransactionStateHistory.builder()
                .previousState(TransactionStatus.PENDING)
                .newState(TransactionStatus.PENDING)
                .transaction(transactionMapper.toEntity(transactionDTO))
                .build();

        transactionStateHistoryRepository.save(transactionStateHistory);

        transactionDTO.setTransactionStatus(TransactionStatus.PENDING);

        saveTransaction(transactionDTO);

        var recipientBalance = findAccountBalance(recipient, transactionDTO.getCurrency());
        recipientBalance.setBalance(recipientBalance.getBalance().add(transferAmount));

        return transactionDTO;
    }

    private AccountBalance findAccountBalance(UserDTO userDTO, Currency currency) {
        return userDTO.getAccountBalances()
                .stream()
                .filter(balance -> balance.getCurrency() == currency)
                .findFirst()
                .orElseThrow(() -> new HttpBadRequestException("Account balance not found"));
    }

}
