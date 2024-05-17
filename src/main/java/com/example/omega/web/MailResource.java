package com.example.omega.web;

import com.example.omega.service.MailService;
import com.example.omega.service.UserService;
import com.example.omega.service.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/mail")
@Slf4j
public class MailResource {

    private final MailService mailService;

    private final UserService userService;

    //TODO: Maybe use securityUtils.canCurrentUserEditThisData
    // User can prompt send verificationCode to another Mail?
    // @PreAuthorize ROLE_USER, ROLE_ADMIN
    @PostMapping("/verification-code")
    public void sendEmail(@RequestBody String email) {
        var user = userService.getUserByEmail(email);

        if (user.isEmpty()) {
            throw new BadRequestException(String.format("User with email %s doesn't exist", email));
        }

        mailService.sendVerificationCodeEmail(email.trim(), user);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        var user = userService.findUserByEmailVerificationToken(token);
        userService.activateUser(user);
        return ResponseEntity.ok("Email verified successfully");
    }

}
