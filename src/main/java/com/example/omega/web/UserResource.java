package com.example.omega.web;

import com.example.omega.service.UserService;
import com.example.omega.service.dto.*;
import com.example.omega.service.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
@Slf4j
public class UserResource {

    private final UserService userService;

    private final SecurityUtils securityUtils;

    //TODO: deleting done by ROLE_ADMIN, ROLE_USER if the it's his own profile, add an additional check if the user has money over 0, to be sure the user wont lose money
    //TODO: company getting the deleted money??

    @PatchMapping("/update/profile")
    @Operation(summary = "Update User non-credential information.")
    public ResponseEntity<UserUpdateDTO> updateUserNonCredentialInformation(Principal principal, @RequestBody UserUpdateDTO userUpdateDTO) {
        securityUtils.canCurrentUserEditThisData(principal, userUpdateDTO.getId());
        var updatedUser = userService.partiallyUpdateUserNonCredentialInformation(userUpdateDTO);
        return ResponseEntity.ok().body(updatedUser);
    }

    @PutMapping("/update/security")
    @Operation(summary = "Update User security data.")
    public ResponseEntity<UserSecurityUpdateDTO> updateUserSecurityData(Principal principal, @RequestBody UserSecurityUpdateDTO userSecurityUpdateDTO) {
        securityUtils.canCurrentUserEditThisData(principal, userSecurityUpdateDTO.getId());
        var updatedUser = userService.updateUserSecurityData(userSecurityUpdateDTO);
        return ResponseEntity.ok().body(updatedUser);
    }

    @PutMapping("/update/password")
    @Operation(summary = "Update User password.")
    public ResponseEntity<UserPasswordChangeDTO> updateUserPassword(Principal principal, @RequestBody UserPasswordChangeDTO userPasswordChangeDTO) {
        securityUtils.canCurrentUserEditThisData(principal, userPasswordChangeDTO.getId());
        var updatedUser = userService.changePassword(userPasswordChangeDTO);
        return ResponseEntity.ok().body(updatedUser);
    }

    @GetMapping("/user/{nameTag}")
    @Operation(summary = "Get user by nameTag.")
    public ResponseEntity<UserSearchDTO> getUserByNameTag(@PathVariable String nameTag) {
        var user = userService.getUserByNameTag(nameTag);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/users")
    @Operation(summary = "Retrieve a page of all users.")
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable, Principal principal) {
        log.debug("User: {} is trying to read all users data!", principal.getName());
        var usersPage = userService.getAllUsers(pageable);
        return ResponseEntity.ok(usersPage);
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Delete a user by their unique user ID.")
    public ResponseEntity<Void> deleteUserById(Principal principal, @PathVariable Long userId) {
        securityUtils.canCurrentUserEditThisData(principal, userId);
        userService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }


}
