package com.example.omega.service.dto;

import lombok.*;

/**
 * DTO used for the update of user sensitive data.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserCredentialUpdateDTO {

    private Long id;

    private String email;

    private boolean twoFactorAuthentication;

    private String oldPassword;

    private String newPassword;
}
