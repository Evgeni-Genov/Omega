package com.example.omega.service.dto;

import com.example.omega.domain.enumeration.Roles;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {

    private Long id;

    private String userName;

    private String email;

    private String nameTag;

    private Roles roles;

    private boolean twoFactorAuthentication;
}
