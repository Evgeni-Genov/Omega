package com.example.omega.web;

import com.example.omega.service.GoogleAuthenticatorService;
import com.example.omega.service.UserService;
import com.example.omega.service.Views;
import com.example.omega.service.dto.UserDTO;
import com.example.omega.service.exception.BadRequestException;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.zxing.WriterException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/google-authenticator")
@AllArgsConstructor
@Slf4j
public class GoogleAuthenticatorResource {

    private final GoogleAuthenticatorService googleAuthenticatorService;
    private final UserService userService;

    @GetMapping("/generate-secret-key")
    public ResponseEntity<String> generateSecretKey() {
        var secretKey = googleAuthenticatorService.generateSecretKey();
        return ResponseEntity.ok(secretKey);
    }

    @GetMapping("/generate-qr-code")
    public ResponseEntity<byte[]> generateQRCode(@RequestParam String account, @RequestParam String issuer) {
        log.debug("Generating QR code for account {}", account);
        var user = userService.getUserByEmail(account);

        if (StringUtils.isBlank(issuer) || StringUtils.isBlank(account)) {
            throw new BadRequestException("Account and Issuer are required!");
        }

        if (user.isEmpty()) {
            throw new BadRequestException("This email does not exist!");
        }

        var secretKey = googleAuthenticatorService.generateSecretKey();
        userService.updateTwoFactorSecret(user.get(), secretKey);
        var barCodeData = googleAuthenticatorService.getGoogleAuthenticatorBarCode(secretKey, account, issuer);
        try {
            var qrCodeImage = googleAuthenticatorService.createQRCode(barCodeData, 200, 200);
            var baos = new ByteArrayOutputStream();
            ImageIO.write(qrCodeImage, "png", baos);
            var qrCodeBytes = baos.toByteArray();
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCodeBytes);
        } catch (WriterException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@JsonView(Views.TwoFactorSecretView.class) @RequestBody UserDTO userDTO) {
        var twoFactorSecret = userService.getUserById(userDTO.getId()).getTwoFactorSecret();
        var isCodeValid = googleAuthenticatorService.verifyCode(twoFactorSecret, userDTO.getTwoFactorAuthCode());
        if (isCodeValid) {
            userService.updateUserTwoStepVerification(userDTO.getId(), true);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification code!");
        }
    }

}
