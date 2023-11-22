package com.example.omega.config.security.jwt;


import com.example.omega.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * AuthTokenFilter is responsible for processing JWT authentication tokens and setting the
 * user's authentication in the Spring Security context if a valid token is provided.
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private JwtUtils jwtUtils;

    private UserDetailsServiceImpl userDetailsService;

    /**
     * Processes the authentication token in the HTTP request and sets the user's authentication if valid.
     *
     * @param request     The HTTP request.
     * @param response    The HTTP response.
     * @param filterChain The filter chain for processing the request.
     * @throws ServletException If a servlet error occurs.
     * @throws IOException      If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            var jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                var username = jwtUtils.getUsernameFromJwtToken(jwt);
                var userDetails = userDetailsService.loadUserByUsername(username);
                var passwordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                passwordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(passwordAuthenticationToken);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Parses the JWT token from the HTTP request's AUTHORIZATION header.
     *
     * @param request The HTTP request.
     * @return The JWT token if found in the header, or null if not found.
     */
    private String parseJwt(HttpServletRequest request) {
        var headerAuth = request.getHeader("AUTHORIZATION");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("BEARER")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}

