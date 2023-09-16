package com.example.omega.repository;

import com.example.omega.domain.User;
import com.example.omega.domain.UserDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDocumentRepository extends JpaRepository<UserDocument, Long> {

    Optional<UserDocument> findByUser(User user);
}
