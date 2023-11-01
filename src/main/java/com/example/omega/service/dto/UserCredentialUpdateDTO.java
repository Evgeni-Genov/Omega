package com.example.omega.service.dto;

import com.example.omega.service.util.StringNormalizationDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * DTO used for the update of user sensitive data.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserCredentialUpdateDTO extends AbstractAuditingDTO {

    private Long id;

    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String email;

    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String userName;

    private boolean twoFactorAuthentication;

    private String oldPassword;

    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character."
    )
    private String newPassword;
}
