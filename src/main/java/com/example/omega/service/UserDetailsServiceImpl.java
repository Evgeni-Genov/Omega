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

    /**
     * Loads a user's details by their username for authentication purposes.
     * This method checks if the user is blocked due to failed login attempts and throws an exception if so.
     * If the user is found in the repository, their details are returned; otherwise, an exception is thrown.
     *
     * @param username The username of the user to be loaded.
     * @return A {@link UserDetails} object containing the user's information.
     * @throws CustomAuthenticationException If the user is blocked or if the username is not found.
     */
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
