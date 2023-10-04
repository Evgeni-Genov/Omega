package com.example.omega.service.dto;

import jakarta.validation.constraints.Pattern;
import lombok.*;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserCreateDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String userName;

    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character."
    )
    private String password;

    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$")
    private String email;

    private String address;

    private String townOfBirth;

    private String countryOfBirth;

}
