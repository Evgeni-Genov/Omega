package com.example.omega.service.dto;

import com.example.omega.service.util.StringNormalizationDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserSearchDTO extends AbstractAuditingDTO {

    private Long id;

    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String nameTag;
}
