package com.example.omega.service;

import com.example.omega.domain.User;
import com.example.omega.service.exception.BadRequestException;
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


    public MailService(JavaMailSender emailSender,
                       UserService userService) {
        this.emailSender = emailSender;
        this.userService = userService;
    }

    //TODO: email verification, email validation, mailDTO
    public void sendVerificationCodeEmail(String recipient, Optional<User> user) {
        var subject = "Verification Code";
        var verificationCode = userService.returnSavedVerificationCode(user);

        log.debug("Sending email to: {} with verification code {}.", recipient, verificationCode);
        try {
            var message = emailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, false);

            helper.setFrom(sender);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(verificationCode);

            emailSender.send(message);
            log.debug("Sent email to {}", recipient);
        } catch (MessagingException e) {
            throw new BadRequestException(e.getMessage());
        }
    }


}
