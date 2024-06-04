package com.example.omega.service.util;

import com.example.omega.domain.PasswordResetLink;
import com.example.omega.domain.User;
import com.example.omega.repository.PasswordResetLinkRepository;
import com.example.omega.service.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class PasswordResetLinkService {

    private final PasswordResetLinkRepository passwordResetLinkRepository;

    /**
     * Generates a password reset link for the specified user.
     *
     * @param user The user for whom the password reset link is generated.
     * @return The generated password reset link.
     * @throws BadRequestException if the user is not present.
     */
    public PasswordResetLink generatePasswordResetLink(User user) {
        var token = UUID.randomUUID().toString();
        var expirationTime = LocalDateTime.now().plusSeconds(75);
        var expirationInstant = expirationTime.atZone(ZoneId.systemDefault()).toInstant();

        var passwordResetLink = new PasswordResetLink();
        passwordResetLink.setToken("http://localhost:5173/reset-password/" + token);
        passwordResetLink.setExpirationTime(expirationInstant);
        passwordResetLink.setUser(user);

        return passwordResetLinkRepository.save(passwordResetLink);
    }

    public PasswordResetLink validateToken(String token) {
        var passwordResetLink = passwordResetLinkRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid password reset token"));

        if (passwordResetLink.getExpirationTime().isBefore(Instant.now())) {
            throw new BadRequestException("Expired password reset token");
        }

        return passwordResetLink;
    }

    /**
     * Checks if the specified passwordResetLink has expired.
     *
     * @param passwordResetLink The passwordResetLink  to check for expiration.
     * @return {@code true} if the passwordResetLink code has expired, {@code false} otherwise.
     */
    public boolean isExpired(PasswordResetLink passwordResetLink) {
        return Instant.now().isAfter(passwordResetLink.getExpirationTime());
    }
}
