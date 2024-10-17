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

    @JsonView({Views.UpdatePasswordView.class, Views.AllUsersWithDetails.class, Views.UpdateEmailView.class,
            Views.UpdateNonCredentialView.class, Views.PasswordResetView.class, Views.TwoFactorAuthenticationView.class,
            Views.TwoFactorSecretView.class, Views.UpdateUsernameView.class, Views.UpdatePhoneNumberView.class,
            Views.EmailVerificationCodeView.class})
    private Long id;

    @JsonView({Views.CreateView.class, Views.AllUsersWithDetails.class, Views.PersonalView.class, Views.UpdateUsernameView.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String username;

    @JsonView({Views.UpdateUsernameView.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String newUsername;

    @JsonView({Views.CreateView.class, Views.UpdateEmailView.class, Views.AllUsersWithDetails.class, Views.PersonalView.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
    private String email;

    @JsonView({Views.CreateView.class, Views.UpdateEmailView.class, Views.AllUsersWithDetails.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
    private String newEmail;

    @JsonView({Views.CreateView.class, Views.UpdateNonCredentialView.class, Views.AllUsersWithDetails.class, Views.PersonalView.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String firstName;

    @JsonView({Views.CreateView.class, Views.UpdateNonCredentialView.class, Views.AllUsersWithDetails.class, Views.PersonalView.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String lastName;

    @JsonView({Views.UpdateNonCredentialView.class, Views.AllUsersWithDetails.class, Views.PersonalView.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String address;

    @JsonView({Views.UpdateNonCredentialView.class, Views.AllUsersWithDetails.class, Views.PersonalView.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String townOfBirth;

    @JsonView({Views.UpdateNonCredentialView.class, Views.AllUsersWithDetails.class, Views.PersonalView.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String countryOfBirth;

    @JsonView({Views.UpdateNonCredentialView.class, Views.SearchView.class, Views.AllUsersWithDetails.class, Views.PersonalView.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    private String nameTag;

    @JsonView({Views.AllUsersWithDetails.class, Views.UpdatePhoneNumberView.class, Views.PersonalView.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    @Pattern(regexp = "^\\+\\d{1,3}\\d{1,14}(\\s\\d{1,13})?$", message = "Phone number must be a valid international number starting with '+'")
    private String phoneNumber;

    @JsonView({Views.AllUsersWithDetails.class, Views.UpdatePhoneNumberView.class})
    @JsonDeserialize(using = StringNormalizationDeserializer.class)
    @Pattern(regexp = "^\\+\\d{1,3}\\d{1,14}(\\s\\d{1,13})?$", message = "Phone number must be a valid international number starting with '+'")
    private String newPhoneNumber;

    @JsonView({Views.CreateView.class, Views.UpdatePasswordView.class})
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character.")
    private String password;

    @JsonView({Views.UpdatePasswordView.class, Views.PasswordResetView.class})
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character.")
    private String newPassword;

    @JsonView({Views.UpdatePasswordView.class, Views.PasswordResetView.class})
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character.")
    private String confirmNewPassword;

    @JsonView({Views.TwoFactorAuthenticationView.class})
    private String emailVerificationToken;

    private String twoFactorSecret;

    @JsonView({Views.TwoFactorSecretView.class})
    private String twoFactorAuthCode;

    @JsonView({Views.PersonalView.class, Views.SearchView.class})
    private String avatar;

    @JsonView({Views.EmailVerificationCodeView.class})
    private String emailVerificationCode;

    @JsonView({Views.AllUsersWithDetails.class})
    @Enumerated(EnumType.STRING)
    private Roles role;

    @JsonView({Views.AllUsersWithDetails.class})
    private Boolean locked;

    @JsonView({Views.AllUsersWithDetails.class})
    private Boolean enabled;

    @JsonView({Views.AllUsersWithDetails.class, Views.PersonalView.class})
    private Boolean twoFactorAuthentication;

    @JsonView({Views.AllUsersWithDetails.class})
    private Boolean isBudgetingEnabled;

    @JsonView({Views.AllUsersWithDetails.class, Views.PersonalView.class})
    private List<AccountBalance> accountBalances;

    @JsonView({Views.AllUsersWithDetails.class})
    private List<Transaction> outgoingTransactions;

    @JsonView({Views.AllUsersWithDetails.class})
    private List<Transaction> incomingTransactions;
}