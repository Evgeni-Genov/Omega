package com.example.omega.web;

import com.example.omega.config.security.jwt.JwtUtils;
import com.example.omega.config.security.payload.request.LoginRequest;
import com.example.omega.config.security.payload.request.SignupRequest;
import com.example.omega.config.security.payload.response.JwtResponse;
import com.example.omega.config.security.payload.response.MessageResponse;
import com.example.omega.domain.User;
import com.example.omega.domain.UserDetailsImpl;
import com.example.omega.mapper.UserMapper;
import com.example.omega.repository.UserRepository;
import com.example.omega.service.MailService;
import com.example.omega.service.UserDetailsServiceImpl;
import com.example.omega.service.UserService;
import com.example.omega.service.Views;
import com.example.omega.service.exception.BadRequestException;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@Slf4j
@AllArgsConstructor
public class AuthenticationController {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final UserMapper userMapper;

    private final UserService userService;

    private final MailService mailService;

    private final UserDetailsServiceImpl userDetailsService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request) {
//        var userDTO =  userService.getUserById(1044L);
//        var user =  userMapper.toEntity(userDTO);
//        user.setPassword(bCryptPasswordEncoder.encode("Evgeni-Genov1"));
//        userRepository.save(user);

        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            if (!authentication.isAuthenticated()) {
                throw new BadRequestException("Invalid username or password");
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (!userService.isUserEnabled(request.getUsername())) {
                throw new BadRequestException("User is not enabled!");
            }

            var jwt = jwtUtils.generateJwtToken(authentication);
            var jwtRefresh = jwtUtils.generateRefreshJwtToken(authentication);

            var userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();

            var roles = userDetailsImpl.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).toList();

            var tokens = new HashMap<String, String>();
            tokens.put("TOKEN:", jwt);
            tokens.put("REFRESH_TOKEN:", jwtRefresh);

            return ResponseEntity.ok(
                    new JwtResponse(jwt, jwtRefresh,
                            userDetailsImpl.getId(),
                            userDetailsImpl.getUsername(),
                            roles,
                            userDetailsImpl.isTwoFactorAuthentication()));
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid username or password");
        }
    }

    @PostMapping("/signup")
    @JsonView(value = Views.CreateView.class)
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        var user = userMapper.toUserAuth(signUpRequest);
        var token = generateEmailVerificationToken();
        setEmailVerificationToken(user, token);
        userService.createUser(userMapper.toDTO(user));
        var verificationLink = "http://localhost:8080/mail/verify-email?token=" + token;
        mailService.accountActivationEmail(user.getEmail(), verificationLink);
        return ResponseEntity.ok(new MessageResponse("SUCCESSFUL_REGISTRATION"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshJwtToken(@RequestBody String refreshToken) {
        log.info("Generating Refresh Token!");
        try {
            if (jwtUtils.validateJwtToken(refreshToken)) {
                var username = jwtUtils.getUsernameFromJwtToken(refreshToken);
                var userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
                var newToken = jwtUtils.generateJwtToken(userDetails);
                var newRefreshToken = jwtUtils.generateRefreshJwtToken(userDetails);

                return ResponseEntity.ok(
                        new JwtResponse(newToken, newRefreshToken, userDetails.getId(), username,
                                userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList(),
                                userDetails.isTwoFactorAuthentication()));
            } else {
                throw new BadRequestException("Invalid refresh token");
            }
        } catch (Exception e) {
            throw new BadRequestException("Could not refresh token: " + e.getMessage());
        }
    }

    public String generateEmailVerificationToken() {
        return UUID.randomUUID().toString();
    }

    public void setEmailVerificationToken(User user, String token) {
        user.setEmailVerificationToken(token);
    }

}
