package com.example.omega.service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserUpdateDTO {

    private Long id;

    private String userName;

    private String oldPassword;

    private String newPassword;

    private String nameTag;
}
