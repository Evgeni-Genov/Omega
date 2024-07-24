package com.example.omega.repository;

import com.example.omega.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, CrudRepository<User, Long> {

    Optional<User> findByUsername(String userName);

    List<User> findByNameTagContainingIgnoreCase(String nameTag);

    Optional<User> findByNameTag(String nameTag);

    boolean existsByEmail(String email);

    boolean existsByUsername(String userName);

    Optional<User> findOneWithAuthoritiesByUsername(String username);

    Optional<User> findByEmail(String email);

    void deleteByEnabledEquals(Boolean enabled);

    Optional<User> getUserByEmailVerificationTokenEquals(String emailVerificationToken);

    @Query("SELECT u.twoFactorAuthentication FROM User u WHERE u.username = :username")
    boolean isTwoFactorAuthenticationEnabled(@Param("username") String username);

    @Query("SELECT u.email FROM User u WHERE u.username = :username")
    String findEmailByUsername(@Param("username") String username);

//    List<User> findByVerificationCodeExpirationTimeBefore(Instant time);
}
