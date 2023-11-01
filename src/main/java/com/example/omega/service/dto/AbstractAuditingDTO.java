package com.example.omega.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class AbstractAuditingDTO {

    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String createdBy;

    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant createdDate;

    @JsonIgnore
    @JsonProperty(access =JsonProperty.Access.READ_ONLY)
    private String lastModifiedBy;

    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant lastModifiedDate;
}
