package com.example.omega.service;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.example.omega.service.exception.BadRequestException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;

@Service
@Slf4j
public class GoogleAuthenticatorService {

    public String generateSecretKey() {
        var random = new SecureRandom();
        var bytes = new byte[20];
        random.nextBytes(bytes);
        var base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    public String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public BufferedImage createQRCode(String barCodeData, int height, int width) throws WriterException, IOException {
        var out = new ByteArrayOutputStream();
        var qrCodeWriter = new QRCodeWriter();
        var bitMatrix = qrCodeWriter.encode(barCodeData, BarcodeFormat.QR_CODE, width, height);
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);
        var qrCodeBytes = out.toByteArray();
        return ImageIO.read(new ByteArrayInputStream(qrCodeBytes));
    }

    public boolean verifyCode(String secretKey, String code) {
        try {
            var expectedCode = getTOTPCode(secretKey);
            return code.equals(expectedCode);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new BadRequestException("Error generating TOTP code");
        }
    }

    private String getTOTPCode(String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        var totpGenerator = new TimeBasedOneTimePasswordGenerator();
        var base32 = new Base32();
        var keyBytes = base32.decode(secretKey);
        var secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA1");
        var now = Instant.now();

        return String.format("%06d", totpGenerator.generateOneTimePassword(secretKeySpec, now));
    }



}
