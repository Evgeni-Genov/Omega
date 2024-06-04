package com.example.omega.config.security.jwt;


import com.example.omega.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
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
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Processes the authentication token in the HTTP request and sets the user's authentication if valid.
     *
     * @param request     The HTTP request.
     * @param response    The HTTP response.
     * @param filterChain The filter chain for processing the request.
     * @throws ServletException If a servlet error occurs.
     * @throws IOException      If an I/O error occurs.
     */
    //TODO: logging in: Missing access token. Rejecting! we log in though why?
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Authenticating request!");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            var jwtAccessToken = parseJwtAccessToken(request);
            if (jwtAccessToken == null && !isSignUpRequest(request)) {
                log.error("Missing access token. Rejecting!");
                filterChain.doFilter(request, response);
                return;
            }
            if (jwtUtils.validateJwtToken(jwtAccessToken) && !isSignUpRequest(request)) {
                var username = jwtUtils.getUsernameFromJwtToken(jwtAccessToken);
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

    //TODO: this fixes the registration error logs
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        log.info("Authenticating request!");
//        try {
//            if (isSignUpRequest(request)){
//                filterChain.doFilter(request, response);
//                return;
//            }
//            var jwtAccessToken = parseJwtAccessToken(request);
//            if (jwtAccessToken == null) {
//                log.error("Missing access token. Rejecting!");
//                filterChain.doFilter(request, response);
//                return;
//            }
//            if (jwtUtils.validateJwtToken(jwtAccessToken)) {
//                var username = jwtUtils.getUsernameFromJwtToken(jwtAccessToken);
//                var userDetails = userDetailsService.loadUserByUsername(username);
//                var passwordAuthenticationToken =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

//                passwordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

//                SecurityContextHolder.getContext().setAuthentication(passwordAuthenticationToken);
//            }
//        } catch (Exception e) {
//            log.error("Cannot set user authentication: {}", e.getMessage());
//        }
//        filterChain.doFilter(request, response);
//    }

    /**
     * Parses the JWT token from the HTTP request's AUTHORIZATION header.
     *
     * @param request The HTTP request.
     * @return The JWT token if found in the header, or null if not found.
     */
    private String parseJwtAccessToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("AUTHORIZATION");
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    /**
     * Checks if the given HTTP request corresponds to a sign-in request.
     *
     * @param request The HTTPServletRequest object representing the current request.
     * @return {@code true} if the request is a sign-in request, {@code false} otherwise.
     */
    private boolean isSignUpRequest(HttpServletRequest request) {
        return request.getRequestURI().endsWith("/auth/signup") && request.getMethod().equals("POST");
    }
}

