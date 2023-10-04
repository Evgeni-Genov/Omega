package com.example.omega.service;

import com.example.omega.repository.UserRepository;
import com.example.omega.service.exception.HttpBadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.
                findByUsername(username)
                .orElseThrow(() -> new HttpBadRequestException(String.format("User with %s not found!", username)));
    }
}
