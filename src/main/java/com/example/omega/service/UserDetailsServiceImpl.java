package com.example.omega.service;

import com.example.omega.domain.UserDetailsImpl;
import com.example.omega.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(()->new UsernameNotFoundException(String.format("User with username: %s not found!", username)));

        return new UserDetailsImpl(user.getId(), user.getUsername(), user.getPassword(), Set.of(new SimpleGrantedAuthority(user.getRole().name())));
    }
}
