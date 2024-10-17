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
        var subject = "Email Verification Code";
        var verificationCode = userService.returnSavedVerificationCode(user);
        log.debug("Sending email to: {} with verification code {}.", recipient, verificationCode);

        var emailContent = new StringBuilder();
        setEmailContentForVerificationCode(user, emailContent, verificationCode);

        sendEmail(recipient, subject, emailContent.toString());
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

    /**
     * Sets the email content for a verification code email.
     * The email content includes a personalized greeting with the user's username,
     * the verification code, and instructions for completing the login process.
     *
     * @param user The {@link User} object representing the recipient of the email.
     * @param emailContent A {@link StringBuilder} object used to build the email content.
     * @param verificationCode The verification code to be included in the email.
     */
    private static void setEmailContentForVerificationCode(User user, StringBuilder emailContent, String verificationCode) {
        emailContent.append("Dear ").append(user.getUsername()).append(",\n\n");
        emailContent.append("To complete your login please fill in the following verification code:\n\n");
        emailContent.append("Verification Code: ").append(verificationCode).append("\n\n");
        emailContent.append("Please enter this code in the verification screen to login.\n\n");
        emailContent.append("If you did not request this verification code, please ignore this email.\n\n");
        emailContent.append("Best regards,\n");
        emailContent.append("The Omega Team");
    }
}

