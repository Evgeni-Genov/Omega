package com.example.omega.repository;

import com.example.omega.domain.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long>, CrudRepository<VerificationCode, Long> {

    List<VerificationCode> findByExpirationTimeBefore(Instant expirationTime);
}
