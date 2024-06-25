package com.example.omega.service;

import com.example.omega.domain.User;
import com.example.omega.repository.UserRepository;
import com.example.omega.service.exception.BadRequestException;
import com.example.omega.service.util.PasswordResetLinkService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class MailService {

    @Value("${spring.mail.username}")
    private String sender;
    private final JavaMailSender emailSender;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordResetLinkService passwordResetLinkService;

    public MailService(JavaMailSender emailSender,
                       UserService userService,
                       UserRepository userRepository,
                       PasswordResetLinkService passwordResetLinkService) {
        this.emailSender = emailSender;
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordResetLinkService = passwordResetLinkService;
    }

    //TODO: email verification, email validation, mailDTO

    /**
     * Create the verification code email.
     *
     * @param recipient the recipient's email address
     * @param user      the optional user for which the verification code is generated
     */
    public void verificationCodeEmail(String recipient, User user) {
        var subject = "Verification Code";
        var verificationCode = userService.returnSavedVerificationCode(user);
        log.debug("Sending email to: {} with verification code {}.", recipient, verificationCode);

        sendEmail(recipient, subject, verificationCode);
    }


    /**
     * Create the password reset email.
     *
     * @param recipient        the recipient's email address
     * @param verificationLink the link for account activation
     */
    public void accountActivationEmail(String recipient, String verificationLink) {
        var subject = "Account activation";
        var emailText = String.format("Please click the following link to verify your email address: %s", verificationLink);
        log.debug("Sending email to: {} with verification link {}.", recipient, verificationLink);

        sendEmail(recipient, subject, emailText);
    }

    /**
     * Create the password reset email.
     *
     * @param recipient the recipient's email address
     * @param user      the optional user for which the password reset link is generated
     */
    public void passwordResetEmail(String recipient, Optional<User> user) {
        var subject = "Password reset";
        var passwordResetLink = userService.returnSavedPasswordResetLink(user);
        var emailText = String.format("Please click the following link to reset your password: %s", passwordResetLink);
        log.debug("Sending email to: {} with password reset link {}", recipient, passwordResetLink);

        sendEmail(recipient, subject, emailText);
    }

    /**
     * Sends an email with the specified recipient, subject, and text.
     *
     * @param recipient the recipient's email address
     * @param subject   the subject of the email
     * @param text      the text content of the email
     */
    private void sendEmail(String recipient, String subject, String text) {
        try {
            var message = emailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, false);

            helper.setFrom(sender);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(text);

            emailSender.send(message);
            log.debug("Sent email to {}", recipient);
        } catch (MessagingException e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}

