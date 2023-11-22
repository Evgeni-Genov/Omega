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

    @PostMapping("/register")
    @Operation(summary = "Register User.")
    public ResponseEntity<UserCreateDTO> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        var createdUser = userService.createUser(userCreateDTO);
        return ResponseEntity.ok().body(createdUser);
    }

    @PatchMapping("/update/profile")
    @Operation(summary = "Update User non-credential information.")
    public ResponseEntity<UserUpdateDTO> updateUserNonCredentialInformation(@RequestBody UserUpdateDTO userUpdateDTO) {
        var updatedUser = userService.partiallyUpdateUserNonCredentialInformation(userUpdateDTO);
        return ResponseEntity.ok().body(updatedUser);
    }

    @PutMapping("/update/security")
    @Operation(summary = "Update User security data.")
    public ResponseEntity<UserSecurityUpdateDTO> updateUserSecurityData(@RequestBody UserSecurityUpdateDTO userSecurityUpdateDTO) {
        var updatedUser = userService.updateUserSecurityData(userSecurityUpdateDTO);
        return ResponseEntity.ok().body(updatedUser);
    }

    @PutMapping("/update/password")
    @Operation(summary = "Update User password.")
    public ResponseEntity<UserPasswordChangeDTO> updateUserPassword(@RequestBody UserPasswordChangeDTO userPasswordChangeDTO) {
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
        var user = SecurityUtils.getCurrentUserLogin();
        var usersPage = userService.getAllUsers(pageable);
        return ResponseEntity.ok(usersPage);
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Delete a user by their unique user ID.")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
        userService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }


}
