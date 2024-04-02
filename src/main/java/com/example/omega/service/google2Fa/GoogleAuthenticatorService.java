package com.example.omega.service.google2Fa;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import de.taimos.totp.TOTP;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;

@Service
@Slf4j
public class GoogleAuthenticatorService {

    private static final String EVERY_25_SECONDS = "*/30 * * * * *";

    @Value("${google-authenticator.secret-phone}")
    private String secretKey;

    public String generateSecretKey() {
        var random = new SecureRandom();
        var bytes = new byte[20];
        random.nextBytes(bytes);
        var base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    @Scheduled(cron = EVERY_25_SECONDS)
    public void getTOTPCode() {
//        var secretKey = "OAXOXTB44MFXVS6PRMH7HQFQCCTZEANG";
        var base32 = new Base32();
        var bytes = base32.decode(secretKey);
        var hexKey = Hex.encodeHexString(bytes);
        log.debug(TOTP.getOTP(hexKey));
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

//    public boolean verifyCode(String secretKey, String code) {
//        var expectedCode = getTOTPCode(secretKey);
//        return code.equals(expectedCode);
//    }

//    public boolean simulateLogin(String secretKey) {
//        var scanner = new Scanner(System.in);
//        System.out.println("Enter the 6-digit code from Google Authenticator:");
//        var code = scanner.nextLine();
//        return verifyCode(secretKey, code);
//    }

}
