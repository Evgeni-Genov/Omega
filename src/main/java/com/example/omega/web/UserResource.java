package com.example.omega.web;

import com.example.omega.service.UserService;
import com.example.omega.service.Views;
import com.example.omega.service.dto.UserDTO;
import com.example.omega.service.util.PaginationUtil;
import com.example.omega.service.util.SecurityUtils;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
@Slf4j
public class UserResource {

    private final UserService userService;

    private final SecurityUtils securityUtils;

    //TODO: deleting done by ROLE_ADMIN, ROLE_USER if the it's his own profile, add an additional check if the user has money over 0, to be sure the user wont lose money
    //TODO: company getting the deleted money??
    //TODO: good policy to suggest the user to send to someone or spend his money
    //TODO: maybe after when having 5 USD user can delete but again with the prompt if user agrees he can delete it.

    @PatchMapping("/update/profile")
    @Operation(summary = "Update User non-credential information.")
    @JsonView({Views.UpdateNonCredentialView.class})
    public ResponseEntity<UserDTO> updateUserNonCredentialInformation(Principal principal, @RequestBody UserDTO userDTO) {
        log.debug("User: {} is trying to update the non-credential data of a user!", principal.getName());
        securityUtils.canCurrentUserEditThisData(principal, userDTO.getId());
        var updatedUser = userService.partiallyUpdateUserNonCredentialInformation(userDTO);
        return ResponseEntity.ok().body(updatedUser);
    }

    @PutMapping("/update/security")
    @Operation(summary = "Update User security data.")
    @JsonView({Views.SecurityUpdateView.class})
    public ResponseEntity<UserDTO> updateUserSecurityData(Principal principal, @RequestBody UserDTO userDTO) {
        log.debug("User: {} is trying to update the security data of a user!", principal.getName());
        securityUtils.canCurrentUserEditThisData(principal, userDTO.getId());
        var updatedUser = userService.updateUserSecurityData(userDTO);
        return ResponseEntity.ok().body(updatedUser);
    }

    @PutMapping("/update/password")
    @Operation(summary = "Update User password.")
    public ResponseEntity<UserDTO> updateUserPassword(Principal principal,
                                                      @JsonView(Views.PasswordChangeView.class) @RequestBody UserDTO userDTO) {
        log.debug("User: {} is trying to update the password of a user!", principal.getName());
        securityUtils.canCurrentUserEditThisData(principal, userDTO.getId());
        var updatedUser = userService.changePassword(userDTO);
        return ResponseEntity.ok().body(updatedUser);
    }

    @GetMapping("/user/{nameTag}")
    @Operation(summary = "Get user by nameTag.")
    @JsonView(Views.SearchView.class)
    public ResponseEntity<UserDTO> getUserByNameTag(@PathVariable String nameTag) {
        var user = userService.getUserByNameTag(nameTag);
        return ResponseEntity.ok().body(user);
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

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Delete a user by their unique user ID.")
    public ResponseEntity<Void> deleteUserById(Principal principal, @PathVariable Long userId) {
        log.debug("User: {} is trying to delete a user by id!", principal.getName());
        securityUtils.canCurrentUserEditThisData(principal, userId);
        userService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

}
