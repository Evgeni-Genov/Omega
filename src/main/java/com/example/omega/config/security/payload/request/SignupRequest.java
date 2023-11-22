package com.example.omega.config.security.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SignupRequest {

    @NotNull
    private String username;

    private String firstName;

    private String lastName;

    @NotNull
    private String password;

}
