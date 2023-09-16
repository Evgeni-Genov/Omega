package com.example.omega.repository;

import com.example.omega.domain.Transaction;
import com.example.omega.domain.User;
import com.example.omega.domain.enumeration.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByRecipient(User recipient);

    List<Transaction> findAllBySender(User sender);

    List<Transaction> findAllByTransactionStatus(TransactionStatus transactionStatus);
}
