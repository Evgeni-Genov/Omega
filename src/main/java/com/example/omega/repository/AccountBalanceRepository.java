package com.example.omega.repository;

import com.example.omega.domain.AccountBalance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountBalanceRepository extends JpaRepository<AccountBalance, Long> {

    @Query("SELECT ab FROM AccountBalance ab WHERE ab.user.id = :userId")
    Page<AccountBalance> findAllByUserId(@Param("userId") Long userId, Pageable pageable);
}

