package com.example.omega.service.dto;

import com.example.omega.service.util.StringNormalizationDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

/**
 * Using this DTO for the update of non-credential user fields.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserUpdateDTO extends AbstractAuditingDTO{

    private Long id;

    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String firstName;

    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String lastName;

    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String phoneNumber;

    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String address;

    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String townOfBirth;

    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String countryOfBirth;

    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String nameTag;
}
