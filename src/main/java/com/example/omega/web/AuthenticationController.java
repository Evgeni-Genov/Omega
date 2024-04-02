package com.example.omega.web;

import com.example.omega.config.security.jwt.JwtUtils;
import com.example.omega.config.security.payload.request.LoginRequest;
import com.example.omega.config.security.payload.request.SignupRequest;
import com.example.omega.config.security.payload.response.JwtResponse;
import com.example.omega.config.security.payload.response.MessageResponse;
import com.example.omega.domain.UserDetailsImpl;
import com.example.omega.mapper.UserMapper;
import com.example.omega.service.UserService;
import com.example.omega.service.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@Slf4j
@AllArgsConstructor
public class AuthenticationController {

    private AuthenticationManager authenticationManager;

    private JwtUtils jwtUtils;

    private UserMapper userMapper;

    private UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request) {

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

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
                        roles));
    }

    @PostMapping("/signup")
    @JsonView(value = Views.CreateView.class)
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        var user = userMapper.toUserAuth(signUpRequest);
        userService.createUser(userMapper.toDTO(user));
        return ResponseEntity.ok(new MessageResponse("SUCCESSFUL_REGISTRATION"));
    }

}
