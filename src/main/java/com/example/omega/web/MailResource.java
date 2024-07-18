package com.example.omega.web;

import com.example.omega.service.MailService;
import com.example.omega.service.UserService;
import com.example.omega.service.util.PasswordResetLinkService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/mail")
@Slf4j
public class MailResource {

    private final MailService mailService;
    private final UserService userService;
    private final PasswordResetLinkService passwordResetLinkService;

    //TODO: Maybe use securityUtils.canCurrentUserEditThisData
    // User can prompt send verificationCode to another Mail?
    // @PreAuthorize ROLE_USER, ROLE_ADMIN


    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        var user = userService.findUserByEmailVerificationToken(token);
        userService.activateUser(user);
        return ResponseEntity.ok("Email verified successfully");
    }

}


