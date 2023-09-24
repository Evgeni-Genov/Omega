package com.example.omega.service.dto;

import lombok.*;

/**
 * Using this DTO for the update of non-credential user fields.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserUpdateDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String address;

    private String townOfBirth;

    private String countryOfBirth;

    private String nameTag;
}
