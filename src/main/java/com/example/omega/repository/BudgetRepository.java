package com.example.omega.repository;

import com.example.omega.domain.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findTopByUserIdOrderByEndDateDesc(Long userId);

    Optional<Budget> findByUserId(Long userId);
}
