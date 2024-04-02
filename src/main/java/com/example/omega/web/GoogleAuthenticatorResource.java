package com.example.omega.web;

import com.example.omega.service.GoogleAuthenticatorService;
import com.google.zxing.WriterException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/google-authenticator")
@AllArgsConstructor
public class GoogleAuthenticatorResource {

    private GoogleAuthenticatorService googleAuthenticatorService;

    @GetMapping("/generate-secret-key")
    public ResponseEntity<String> generateSecretKey() {
        String secretKey = googleAuthenticatorService.generateSecretKey();
        return ResponseEntity.ok(secretKey);
    }

    @GetMapping("/generate-qr-code")
    public ResponseEntity<byte[]> generateQRCode(@RequestParam String account, @RequestParam String issuer) {
        if (account == null || issuer == null) {
            return ResponseEntity.badRequest().body("Account and issuer are required".getBytes());
        }
        String secretKey = googleAuthenticatorService.generateSecretKey();
        String barCodeData = googleAuthenticatorService.getGoogleAuthenticatorBarCode(secretKey, account, issuer);
        try {
            BufferedImage qrCodeImage = googleAuthenticatorService.createQRCode(barCodeData, 200, 200);
            var baos = new ByteArrayOutputStream();
            ImageIO.write(qrCodeImage, "png", baos);
            var qrCodeBytes = baos.toByteArray();
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCodeBytes);
        } catch (WriterException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @GetMapping("/simulate-login")
//    public ResponseEntity<String> simulateLogin(@RequestParam String secretKey) {
//        var loggedIn = googleAuthenticatorService.simulateLogin(secretKey);
//        if (loggedIn) {
//            return ResponseEntity.ok("Login successful");
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid 2FA code");
//        }
//    }
}
