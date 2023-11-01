package com.example.omega.service.dto;

import com.example.omega.service.util.StringNormalizationDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Pattern;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserCreateDTO extends AbstractAuditingDTO {

    private Long id;

    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String firstName;

    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String lastName;

    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String userName;

    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character."
    )
    private String password;

    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$")
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String email;

    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String address;

    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String townOfBirth;

    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String countryOfBirth;

}
