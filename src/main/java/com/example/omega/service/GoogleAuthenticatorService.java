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

    /**
     * Generates a secret key for use with TOTP (Time-based One-Time Password).
     * <p>
     * This method creates a random 20-byte secret key and encodes it using Base32.
     *
     * @return the generated secret key as a Base32-encoded {@link String}
     */
    public String generateSecretKey() {
        var random = new SecureRandom();
        var bytes = new byte[20];
        random.nextBytes(bytes);
        var base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    /**
     * Creates a Google Authenticator-compatible TOTP URI (barcode data).
     * <p>
     * The URI contains the account name, the issuer, and the secret key, formatted for
     * compatibility with Google Authenticator.
     *
     * @param secretKey the Base32-encoded secret key
     * @param account   the account name (e.g., email)
     * @param issuer    the issuer name (e.g., company or application name)
     * @return a TOTP URI that can be converted to a QR code
     */
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

    /**
     * Creates a QR code image from a given TOTP URI (barcode data).
     * <p>
     * This method generates a QR code image in PNG format from the provided barcode data.
     *
     * @param barCodeData the TOTP URI to be encoded as a QR code
     * @param height the height of the resulting QR code image
     * @param width the width of the resulting QR code image
     * @return a {@link BufferedImage} representing the QR code
     * @throws WriterException if an error occurs while generating the QR code
     * @throws IOException if an I/O error occurs during image generation
     */
    public BufferedImage createQRCode(String barCodeData, int height, int width) throws WriterException, IOException {
        var out = new ByteArrayOutputStream();
        var qrCodeWriter = new QRCodeWriter();
        var bitMatrix = qrCodeWriter.encode(barCodeData, BarcodeFormat.QR_CODE, width, height);
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);
        var qrCodeBytes = out.toByteArray();
        return ImageIO.read(new ByteArrayInputStream(qrCodeBytes));
    }

    /**
     * Verifies the provided TOTP code against the generated TOTP code for a given secret key.
     * <p>
     * This method generates the expected TOTP code from the secret key and compares it to
     * the provided code to verify its correctness.
     *
     * @param secretKey the Base32-encoded secret key used for TOTP generation
     * @param code the TOTP code to be verified
     * @return {@code true} if the provided code matches the generated code; {@code false} otherwise
     * @throws BadRequestException if an error occurs while generating the TOTP code
     */
    public boolean verifyCode(String secretKey, String code) {
        try {
            var expectedCode = getTOTPCode(secretKey);
            return code.equals(expectedCode);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new BadRequestException("Error generating TOTP code");
        }
    }

    /**
     * Generates a TOTP (Time-based One-Time Password) code for the given secret key.
     * <p>
     * This method uses the HMAC-SHA1 algorithm to generate a TOTP code based on the current
     * time and the provided secret key.
     *
     * @param secretKey the Base32-encoded secret key used for TOTP generation
     * @return the generated TOTP code as a {@link String}
     * @throws NoSuchAlgorithmException if the HMAC-SHA1 algorithm is not available
     * @throws InvalidKeyException if the provided secret key is invalid
     */
    private String getTOTPCode(String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        var totpGenerator = new TimeBasedOneTimePasswordGenerator();
        var base32 = new Base32();
        var keyBytes = base32.decode(secretKey);
        var secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA1");
        var now = Instant.now();

        return String.format("%06d", totpGenerator.generateOneTimePassword(secretKeySpec, now));
    }
}
