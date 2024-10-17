package com.example.omega.repository;

import com.example.omega.domain.Transaction;
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

    List<Transaction> findByCreatedDateBetweenAndSenderId(Instant startDateTime, Instant endDateTime, Long senderId);

    @Query("SELECT t FROM Transaction t WHERE (t.recipient.id = :userId OR t.sender.id = :userId) AND t.createdDate BETWEEN :startDateTime AND :endDateTime")
    List<Transaction> findByUserIdAndCreatedDateBetween(@Param("userId") Long userId, @Param("startDateTime") Instant startDateTime, @Param("endDateTime") Instant endDateTime);

    @Query(value = "SELECT transaction FROM Transaction transaction " +
            "WHERE transaction.sender.id = :userId OR transaction.recipient.id = :userId " +
            "ORDER BY transaction.createdDate DESC")
    Page<Transaction> findAllByRecipientAndSender(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE (t.sender.id = :userId AND t.recipient.id = :otherUserId) OR (t.sender.id = :otherUserId AND t.recipient.id = :userId) ORDER BY t.lastModifiedDate ASC")
    Page<Transaction> findByUserIdAndOtherUserId(@Param("userId") Long userId, @Param("otherUserId") Long otherUserId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.recipient.id = :userId AND t.transactionStatus = 'PENDING' AND t.transactionType = 'TRANSFER'")
    List<Transaction> findPendingFundRequestsForUser(@Param("userId") Long userId);
}
