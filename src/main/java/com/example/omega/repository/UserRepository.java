package com.example.omega.repository;

import com.example.omega.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String userName);

    Optional<User> findByNameTag(String nameTag);

    boolean existsByEmail(String email);

    boolean existsByUsername(String userName);
}
