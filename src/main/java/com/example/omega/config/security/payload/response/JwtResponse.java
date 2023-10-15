package com.example.omega.config.security.payload.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString

public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private String refreshToken;
    private Long id;
    private String username;
    private List<String> roles;

    public JwtResponse(String accessToken, String refreshToken, Long id, String username, List<String> roles) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.username = username;
        this.roles = roles;
    }
}
