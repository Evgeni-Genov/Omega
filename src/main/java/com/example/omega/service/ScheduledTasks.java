package com.example.omega.service;

import com.example.omega.domain.enumeration.TransactionStatus;
import com.example.omega.repository.PasswordResetLinkRepository;
import com.example.omega.repository.TransactionRepository;
import com.example.omega.repository.UserRepository;
import com.example.omega.repository.VerificationCodeRepository;
import de.taimos.totp.TOTP;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static com.example.omega.service.util.Constants.*;

@Slf4j
@Component
public class ScheduledTasks {

    private final VerificationCodeService verificationCodeService;
    @Value("${google-authenticator.secret-phone}")
    private String secretKey;
    private final VerificationCodeRepository verificationCodeRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final PasswordResetLinkRepository passwordResetLinkRepository;

    public ScheduledTasks(VerificationCodeRepository verificationCodeRepository, TransactionRepository transactionRepository,
                          UserRepository userRepository, PasswordResetLinkRepository passwordResetLinkRepository, VerificationCodeService verificationCodeService) {
        this.verificationCodeRepository = verificationCodeRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.passwordResetLinkRepository = passwordResetLinkRepository;
        this.verificationCodeService = verificationCodeService;
    }

    /**
     * Scheduled task to log the codes from Google Authenticator.
     * Currently, enabled for the Samsung Galaxy Device.
     */
    @Scheduled(cron = EVERY_25_SECONDS)
    public void getTOTPCode() {
        var base32 = new Base32();
        var bytes = base32.decode(secretKey);
        var hexKey = Hex.encodeHexString(bytes);
        log.debug(TOTP.getOTP(hexKey));
    }

    /**
     * Scheduled task to delete expired verification codes from the repository.
     * This task runs periodically according to the specified cron expression.
     */
    @Scheduled(cron = EVERY_TWO_MINUTES)
    @Transactional
    public void deleteExpiredVerificationCodes() {
        log.debug("Starting deletion of all expired Verification Codes!");
        var now = Instant.now();
        verificationCodeService.deleteExpiredVerificationCodes(now);
        log.debug("Deletion of all expired Verification Codes completed!");
    }

    /**
     * Deletes all transactions with a status of 'SUCCESSFUL' that are older than the start of the previous week,
     * scheduled to run every Sunday at 1:00 AM.
     */
    @Scheduled(cron = EVERY_SUNDAY_1_AM)
    @Transactional
    public void deleteExpiredTransactions() {
        log.debug("Starting deletion all expired Transactions!");
        var lastMonday = LocalDate.now(ZoneOffset.UTC)
                .minusWeeks(1)
                .with(DayOfWeek.MONDAY);
        var lastMondayToInstant = lastMonday.atStartOfDay(ZoneOffset.UTC).toInstant();
        transactionRepository.deleteAllTransactionsWithStatusOlderThan(TransactionStatus.SUCCESSFUL.name(), lastMondayToInstant);
        log.debug("Deletion of all expired Transactions completed!");
    }

    @Scheduled(cron = EVERY_DAY_3_AM)
    @Transactional
    public void deleteAllNonActivatedUsers(){
        log.debug("Starting deletion of all non activated users!");
        userRepository.deleteByEnabledEquals(false);
        log.debug("Deletion of all non activated users completed!");
    }


    @Scheduled(cron = EVERY_TWO_MINUTES)
    @Transactional
    public void deleteExpiredPasswordResetLink() {
        log.debug("Starting deletion of all expired Password Reset Codes!");
        var now = Instant.now();
        passwordResetLinkRepository.deleteByExpirationTimeBefore(now);
        log.debug("Deletion of all expired Password Reset Links completed!");
    }

}
