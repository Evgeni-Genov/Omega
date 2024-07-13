package com.example.omega.web;

import com.example.omega.service.MailService;
import com.example.omega.service.UserService;
import com.example.omega.service.Views;
import com.example.omega.service.dto.UserDTO;
import com.example.omega.service.exception.BadRequestException;
import com.example.omega.service.util.PaginationUtil;
import com.example.omega.service.util.PasswordResetLinkService;
import com.example.omega.service.util.SecurityUtils;
import com.example.omega.service.util.UserServiceUtil;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

import static com.example.omega.service.util.Constants.USER_PROFILE_DIR;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
@Slf4j
public class UserResource {

    private final UserService userService;
    private final UserServiceUtil userServiceUtil;
    private final SecurityUtils securityUtils;
    private final PasswordResetLinkService passwordResetLinkService;
    private final MailService mailService;

    //TODO: deleting done by ROLE_ADMIN, ROLE_USER if the it's his own profile, add an additional check if the user has money over 0, to be sure the user wont lose money
    //TODO: good policy to suggest the user to send to someone or spend his money
    //TODO: maybe after when having 5 USD user can delete but again with the prompt if user agrees he can delete it.

    @PatchMapping("/update/profile")
    @Operation(summary = "Update User non-credential information.")
    @JsonView(Views.UpdateNonCredentialView.class)
    public ResponseEntity<UserDTO> updateUserNonCredentialInformation(Principal principal,
                                                                      @JsonView(Views.UpdateNonCredentialView.class) @RequestBody UserDTO userDTO) {
        log.debug("User: {} is trying to update the non-credential data of a user!", principal.getName());
        securityUtils.canCurrentUserAccessThisData(principal, userDTO.getId());
        var updatedUser = userService.partiallyUpdateUserNonCredentialInformation(userDTO);
        return ResponseEntity.ok().body(updatedUser);
    }

    @PutMapping("/update/email")
    @Operation(summary = "Update User email.")
    @JsonView(Views.UpdateEmailView.class)
    public ResponseEntity<UserDTO> updateUserEmail(Principal principal,
                                                   @JsonView(Views.UpdateEmailView.class) @RequestBody UserDTO userDTO) {
        log.debug("User: {} is trying to update the security data of a user!", principal.getName());
        securityUtils.canCurrentUserAccessThisData(principal, userDTO.getId());
        var updatedUser = userService.updateUserEmail(userDTO);
        return ResponseEntity.ok().body(updatedUser);
    }

    @PutMapping("/update/password")
    @Operation(summary = "Update User password.")
    @JsonView(Views.UpdatePasswordView.class)
    public ResponseEntity<UserDTO> updateUserPassword(Principal principal,
                                                      @JsonView(Views.UpdatePasswordView.class) @RequestBody UserDTO userDTO) {
        log.debug("User: {} is trying to update the password of a user!", principal.getName());
        securityUtils.canCurrentUserAccessThisData(principal, userDTO.getId());
        var updatedUser = userService.changePassword(userDTO);
        return ResponseEntity.ok().body(updatedUser);
    }

    @GetMapping("/search-user/{nameTag}")
    @Operation(summary = "Get users by nameTag containing.")
    @JsonView(Views.SearchView.class)
    public ResponseEntity<List<UserDTO>> getUsersByNameTag(@PathVariable String nameTag) {
        var users = userService.getUsersByNameTagContaining(nameTag);
        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/users")
    @Operation(summary = "Retrieve a page of all users.")
    @JsonView(Views.AllUsersWithDetails.class)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers(Pageable pageable, Principal principal) {
        log.debug("User: {} is trying to read all users data!", principal.getName());
        var usersPage = userService.getAllUsers(pageable);
        var headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), usersPage);
        return ResponseEntity.ok().headers(headers).body(usersPage.getContent());
    }

    @GetMapping("/user/{id}")
    @Operation(summary = "Get user by id.")
    @JsonView(Views.PersonalView.class)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id, Principal principal) {
        log.debug("User: {} is trying to read user by id!", principal.getName());
        securityUtils.canCurrentUserAccessThisData(principal, id);
        var user = userService.getUserById(id);
        return ResponseEntity.ok().body(user);
    }

    @DeleteMapping("/delete-user/{userId}")
    @Operation(summary = "Delete a user by their unique user ID.")
    public ResponseEntity<Void> deleteUserById(Principal principal, @PathVariable Long userId) {
        log.debug("User: {} is trying to delete a user by id!", principal.getName());
        securityUtils.canCurrentUserAccessThisData(principal, userId);
        userService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public void passwordReset(@RequestParam String email) {
        log.debug("User: {} is trying to reset the password!", email);
        var user = userService.getUserByEmail(email);

        if (user.isEmpty()) {
            throw new BadRequestException(String.format("User with email %s doesn't exist!", email));
        }

        mailService.passwordResetEmail(email.trim(), user);
    }

    @PostMapping("/reset-password/confirm")
    @JsonView(Views.PasswordResetView.class)
    public void confirmPasswordReset(@RequestParam String token,
                                     @JsonView(Views.PasswordResetView.class) @RequestBody UserDTO userDTO) {
        log.debug("Confirm password reset!");
        var passwordResetLink = passwordResetLinkService.validateToken(token);

        if (passwordResetLink == null || passwordResetLinkService.isExpired(passwordResetLink)) {
            throw new BadRequestException("Invalid or expired password reset token");
        }

        userService.passwordReset(passwordResetLink.getUser(), userDTO);
    }

    @PostMapping("/update-2fa")
    public ResponseEntity<?> updateTwoFactorAuthentication(@RequestBody UserDTO userDTO) {
        log.debug("User: {} is trying to update two-factor authentication.", userDTO.getEmail());
        userService.updateUserTwoStepVerification(userDTO.getId(), userDTO.getTwoFactorAuthentication());
        return ResponseEntity.ok().body("Two-factor authentication " + (Boolean.TRUE.equals(userDTO.getTwoFactorAuthentication()) ? "enabled" : "disabled"));
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<UserDTO> uploadAvatar(@RequestParam("file") MultipartFile file, @RequestParam("userId") Long userId, Principal principal) {
        log.debug("User: {} is trying to upload avatar.", principal.getName());
        securityUtils.canCurrentUserAccessThisData(principal, userId);
        var updatedUser = userService.updateUserAvatar(userId, file);
        return ResponseEntity.ok().body(updatedUser);
    }

    @GetMapping("/avatar/user/{userId}")
    public ResponseEntity<byte[]> getAvatarByUserId(@PathVariable Long userId) {
        log.debug("REST request to access avatar for user ID: {}", userId);
        try {
            var user = userService.getUserById(userId);
            var filename = user.getAvatar();
            var fileContent = userService.getAvatarContent(filename);
            var contentType = Files.probeContentType(Paths.get(USER_PROFILE_DIR).resolve(filename));
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(fileContent);
        } catch (Exception e) {
            throw new BadRequestException("Error retrieving avatar for user ID: " + userId);
        }
    }

    @PutMapping("/update/number")
    @JsonView(Views.UpdatePhoneNumberView.class)
    public ResponseEntity<UserDTO> updatePhoneNumber(Principal principal,
                                                     @JsonView(Views.UpdatePhoneNumberView.class) @RequestBody UserDTO userDTO) {
        log.debug("User: {} is trying to update phone number!", principal.getName());
        securityUtils.canCurrentUserAccessThisData(principal, userDTO.getId());
        var updatedUser = userService.updatePhoneNumber(userDTO);
        return ResponseEntity.ok().body(updatedUser);
    }

    //    @PutMapping("/update/username")
    @Operation(summary = "Update User username.")
    @JsonView(Views.UpdateUsernameView.class)
    public ResponseEntity<UserDTO> updateUsername(Principal principal,
                                                  @JsonView(Views.UpdateUsernameView.class) @RequestBody UserDTO userDTO) {
        log.debug("User: {} is trying to update the username of a user!", principal.getName());
        securityUtils.canCurrentUserAccessThisData(principal, userDTO.getId());
        var updatedUser = userService.updateUsername(userDTO);
        return ResponseEntity.ok().body(updatedUser);
    }
}




