package com.example.omega.repository;

import com.example.omega.domain.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long>, CrudRepository<VerificationCode, Long> {

    void deleteByExpirationTimeBefore(Instant expirationTime);
}
