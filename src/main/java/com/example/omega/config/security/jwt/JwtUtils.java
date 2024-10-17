package com.example.omega.config.security.jwt;


import com.example.omega.domain.UserDetailsImpl;
import com.example.omega.service.exception.BadRequestException;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
     * @return The generated JWT token as a String.
     */
    public String generateJwtToken(Authentication authentication) {
        var userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Generates a JWT token using the provided user details.
     *
     * @param userDetails The user details object containing user information.
     * @return The generated JWT token as a String.
     */
    public String generateJwtToken(UserDetailsImpl userDetails) {
        var authorities = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("authorities", authorities)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Generates a refresh JWT token using the provided user details.
     *
     * @param userDetails The user details object containing user information.
     * @return The generated refresh JWT token as a String.
     */
    public String generateRefreshJwtToken(UserDetailsImpl userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtRefreshExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Generates a refresh JWT token for the provided authentication.
     *
     * @param authentication The authentication object containing user details.
     * @return The generated refresh JWT token as a String.
     */
    public String generateRefreshJwtToken(Authentication authentication) {
        var userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtRefreshExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Extracts the username from a JWT token.
     *
     * @param token The JWT token as a String.
     * @return The username extracted from the token.
     * @throws BadRequestException if the token is invalid or cannot be parsed.
     */
    public String getUsernameFromJwtToken(String token) {
        log.info("Parsing token: {}", token);
        try {
            return Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            log.error("Error parsing token: {}", e.getMessage());
            throw new BadRequestException("Invalid token");
        }
    }

    /**
     * Extracts roles from a JWT token.
     *
     * @param token The JWT token as a String.
     * @return The list of roles extracted from the token.
     * @throws BadRequestException if the token is invalid or cannot be parsed.
     */
    public List<?> getRolesFromJwtToken(String token) {
        log.info("Parsing roles from token: {}", token);
        try {
            return Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody()
                    .get("authorities", List.class);
        } catch (JwtException e) {
            log.error("Error parsing authorities from token: {}", e.getMessage());
            throw new BadRequestException("Invalid token");
        }
    }

    /**
     * Validates a JWT token for integrity, expiration, and other potential issues.
     *
     * @param authToken The JWT token to be validated.
     * @return True if the token is valid, false otherwise.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            throw new BadRequestException("JWT token is expired");
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

}
