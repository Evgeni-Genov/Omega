package com.example.omega.repository;

import com.example.omega.domain.Transaction;
import com.example.omega.domain.User;
import com.example.omega.domain.enumeration.TransactionStatus;
import jakarta.transaction.Transactional;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

@JaversSpringDataAuditable
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Transaction t WHERE t.transactionStatus = :transactionStatus AND t.createdDate <= :timestamp")
    void deleteAllTransactionsWithStatusOlderThan(@Param("transactionStatus") String transactionStatus, @Param("timestamp") Instant timestamp);

    List<Transaction> findAllByRecipient(User recipient);

    List<Transaction> findAllBySender(User sender);

    List<Transaction> findAllByTransactionStatus(TransactionStatus transactionStatus);

    @Query(value = "SELECT transaction FROM Transaction transaction " +
            "WHERE transaction.sender.id = :userId OR transaction.recipient.id = :userId " +
            "ORDER BY transaction.createdDate DESC")
    Page<Transaction> findAllByRecipientAndSender(@Param("userId") Long userId, Pageable pageable);

    Page<Transaction> findAllByCreatedDate(TransactionStatus transactionStatus, Pageable pageable);

    Page<Transaction> findAllByCreatedDateOrderByCreatedDateDesc(TransactionStatus transactionStatus, Pageable pageable);
}
