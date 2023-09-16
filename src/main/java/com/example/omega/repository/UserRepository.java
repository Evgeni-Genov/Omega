package com.example.omega.repository;

import com.example.omega.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNameTag(String nameTag);

    Optional<User> findByUserName(String userName);
}
