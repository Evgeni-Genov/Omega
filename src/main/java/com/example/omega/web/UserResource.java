package com.example.omega.web;

import com.example.omega.service.UserService;
import com.example.omega.service.dto.UserCreateDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class UserResource {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Get all Dunning Overviews by criteria")
    public ResponseEntity<UserCreateDTO> createUser(@RequestBody UserCreateDTO userCreateDTO){
        var createdUser = userService.createUser(userCreateDTO);
        return ResponseEntity.ok().body(createdUser);
    }

}
