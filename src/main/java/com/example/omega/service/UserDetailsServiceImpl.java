package com.example.omega.service;

import com.example.omega.config.security.LoginAttemptService;
import com.example.omega.domain.UserDetailsImpl;
import com.example.omega.repository.UserRepository;
import com.example.omega.service.exception.CustomAuthenticationException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws CustomAuthenticationException {
        if (loginAttemptService.isBlocked()) {
            throw new CustomAuthenticationException("You have been blocked! Nice Try :)");
        }

        var user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new CustomAuthenticationException(String.format("User with username: %s not found!", username)));
        return new UserDetailsImpl(user);
    }
}
