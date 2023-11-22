package com.example.omega.config.security.jwt;


import com.example.omega.domain.UserDetailsImpl;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtUtils {

    @Value("${omega.app.jwt-secret}")
    private String jwtSecret;

    @Value("${omega.app.jwt-expiration-ms}")
    private int jwtExpiration;

    @Value("${omega.app.jwt-refresh-expirations-ms}")
    private int jwtRefreshExpiration;

    /**
     * Generates a JWT token for the provided authentication.
     *
     * @param authentication The authentication object containing user details.
     * @return The generated JWT token.
     */
    public String generateJwtToken(Authentication authentication) {
        // Extracts user details from the authentication object.
        var userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        // Builds a JWT token with user subject, issued and expiration dates, and signs it with the specified algorithm and secret.
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Generates a refresh JWT token for the provided authentication.
     *
     * @param authentication The authentication object containing user details.
     * @return The generated refresh JWT token.
     */
    public String generateRefreshJwtToken(Authentication authentication) {
        // Extracts user details from the authentication object.
        var userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        // Builds a refresh JWT token with user subject, issued and expiration dates, and signs it with the specified algorithm and secret.
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtRefreshExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Retrieves the username from a given JWT token.
     *
     * @param token The JWT token.
     * @return The username extracted from the token.
     */
    public String getUsernameFromJwtToken(String token) {
        // Parses the JWT token, retrieves the subject (username) from the claims, and returns it.
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Validates a JWT token for integrity, expiration, and other potential issues.
     *
     * @param authToken The JWT token to be validated.
     * @return True if the token is valid, false otherwise.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            // Parses the JWT token to check for validity. If successful, returns true.
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        // If any exception occurs during validation, returns false.
        return false;
    }
}
