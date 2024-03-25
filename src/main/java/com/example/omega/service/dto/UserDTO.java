package com.example.omega.service.dto;

import com.example.omega.domain.AccountBalance;
import com.example.omega.domain.Transaction;
import com.example.omega.domain.enumeration.Roles;
import com.example.omega.service.Views;
import com.example.omega.service.util.StringNormalizationDeserializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO extends AbstractAuditingDTO {

    @JsonView({Views.PasswordChangeView.class, Views.AllUsersWithDetails.class, Views.SecurityUpdateView.class})
    private Long id;

    @JsonView({Views.CreateView.class, Views.AllUsersWithDetails.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String username;

    @JsonView({Views.CreateView.class, Views.SecurityUpdateView.class, Views.AllUsersWithDetails.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
    private String email;

    @JsonView({Views.CreateView.class, Views.SecurityUpdateView.class, Views.AllUsersWithDetails.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
    private String newEmail;

    @JsonView({Views.CreateView.class, Views.UpdateNonCredentialView.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String firstName;

    @JsonView({Views.CreateView.class, Views.UpdateNonCredentialView.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String lastName;

    @JsonView({Views.UpdateNonCredentialView.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String address;

    @JsonView({Views.UpdateNonCredentialView.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String townOfBirth;

    @JsonView({Views.UpdateNonCredentialView.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String countryOfBirth;

    @JsonView({Views.UpdateNonCredentialView.class, Views.SearchView.class, Views.AllUsersWithDetails.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String nameTag;

    @JsonView({Views.SecurityUpdateView.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    @Pattern(regexp = "^(\\d{3}[- .]?){2}\\d{4}$")
    private String phoneNumber;

    @JsonView({Views.CreateView.class, Views.PasswordChangeView.class})
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character."
    )
    private String password;

    @JsonView({Views.PasswordChangeView.class})
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character."
    )
    private String newPassword;

    @JsonView({Views.AllUsersWithDetails.class})
    @Enumerated(EnumType.STRING)
    private Roles role;

    @JsonView({Views.AllUsersWithDetails.class})
    private Boolean locked;

    @JsonView({Views.AllUsersWithDetails.class})
    private Boolean enabled;

    @JsonView({Views.SecurityUpdateView.class, Views.AllUsersWithDetails.class})
    private Boolean twoFactorAuthentication;

    @JsonView(Views.AllUsersWithDetails.class)
    private List<AccountBalance> accountBalances;

    @JsonView(Views.AllUsersWithDetails.class)
    private List<Transaction> outgoingTransactions;

    @JsonView(Views.AllUsersWithDetails.class)
    private List<Transaction> incomingTransactions;
}