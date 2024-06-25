package com.example.omega.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@AllArgsConstructor
@Getter
public class UserDetailsImpl implements UserDetails {

    private Long id;

    private String username;

    private String password;

    private boolean twoFactorAuthentication;

    private Collection<? extends GrantedAuthority> authorities;

//    private boolean accountNonExpired;
//
//    private boolean accountNonLocked;
//
//    private boolean credentialsNonExpired;
//
//    private boolean enabled;


    public UserDetailsImpl(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.twoFactorAuthentication = user.getTwoFactorAuthentication();
        this.authorities = Set.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
