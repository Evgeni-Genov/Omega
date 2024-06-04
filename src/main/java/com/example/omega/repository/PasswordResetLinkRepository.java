package com.example.omega.repository;

import com.example.omega.domain.PasswordResetLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface PasswordResetLinkRepository extends JpaRepository<PasswordResetLink, Long> {

//    Optional<PasswordResetLink> findByToken(String token);

    @Query("SELECT p FROM PasswordResetLink p WHERE p.token = :token")
    Optional<PasswordResetLink> findByToken(@Param("token") String token);

    void deleteByExpirationTimeBefore(Instant expirationTime);
}
