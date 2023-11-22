package com.example.omega.service.dto;

import com.example.omega.service.util.StringNormalizationDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

/**
 * DTO used for the update of user sensitive data.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserSecurityUpdateDTO extends AbstractAuditingDTO {

    private Long id;

    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String email;

    private boolean twoFactorAuthentication;
}
