package com.example.omega.repository;

import com.example.omega.domain.TransactionStateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionStateHistoryRepository extends JpaRepository<TransactionStateHistory, Long> {
}
