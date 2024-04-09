package com.example.omega.service;

import com.example.omega.domain.User;
import com.example.omega.domain.VerificationCode;
import com.example.omega.repository.VerificationCodeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;

    /**
     * Generates a verification code for the specified user.
     *
     * @param user The user for whom the verification code is generated.
     * @return The generated verification code.
     */
    public VerificationCode generateVerificationCode(User user) {
        // Generate a 6-digit random code
        var code = String.format("%06d", new Random().nextInt(999999) + 1);

        var expirationTime = Instant.now().plus(75, ChronoUnit.SECONDS);

        var verificationCode = new VerificationCode();
        verificationCode.setCode(code);
        verificationCode.setExpirationTime(expirationTime);
        verificationCode.setUser(user);

        return verificationCodeRepository.save(verificationCode);
    }

    /**
     * Checks if the specified verification code has expired.
     *
     * @param verificationCode The verification code to check for expiration.
     * @return {@code true} if the verification code has expired, {@code false} otherwise.
     */
    public boolean isExpired(VerificationCode verificationCode) {
        return Instant.now().isAfter(verificationCode.getExpirationTime());
    }

}
