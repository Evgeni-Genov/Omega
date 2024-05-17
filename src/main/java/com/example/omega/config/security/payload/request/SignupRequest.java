package com.example.omega.config.security.payload.request;

import com.example.omega.service.util.StringNormalizationDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String username;

    @NotNull
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$")
    private String email;

    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String firstName;

    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String lastName;

    @NotNull
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=-])(?=\\S+$).{8,}$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character."
    )
    private String password;

}
