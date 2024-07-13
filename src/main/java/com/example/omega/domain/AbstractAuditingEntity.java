package com.example.omega.domain;

import com.example.omega.service.exception.BadRequestException;
import com.example.omega.service.util.SecurityUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class AbstractAuditingEntity implements Serializable {

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdDate = Instant.now();

    @LastModifiedBy
    @Column
    private String lastModifiedBy;

    @LastModifiedDate
    @Column
    private Instant lastModifiedDate = Instant.now();

//TODO: Uncomment when testing the other is completed
//    @PrePersist
//    public void setCreator() {
//        var currentUser = SecurityUtils.getCurrentUserLogin().orElse("anonymousUser");
//        setCreatedBy(currentUser);
//        setLastModifiedBy(currentUser);
//    }
//TODO: Uncomment when testing the other is completed
//    @PreUpdate
//    public void setChangesAuthor() {
//        var currentUser = SecurityUtils.getCurrentUserLogin().orElse("anonymousUser");
//        setLastModifiedBy(currentUser);
//    }

    /**
     * Sets the creator and last modifier before the entity is persisted.
     * If no authenticated user is found, a BadRequestException is thrown.
     *
     * @throws BadRequestException if no authenticated user is found
     */
    @PrePersist
    public void setCreator() {
        var currentUser = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new BadRequestException("No authenticated user found"));
        setCreatedBy(currentUser);
        setLastModifiedBy(currentUser);
    }

    /**
     * Sets the last modifier before the entity is updated.
     * If no authenticated user is found, a BadRequestException is thrown.
     *
     * @throws BadRequestException if no authenticated user is found
     */
    @PreUpdate
    public void setChangesAuthor() {
        var currentUser = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new BadRequestException("No authenticated user found"));
        setLastModifiedBy(currentUser);
    }

}
