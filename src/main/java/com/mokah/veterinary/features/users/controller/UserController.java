package com.mokah.veterinary.features.users.controller;

import com.mokah.veterinary.features.users.dto.UserResponse;
import com.mokah.veterinary.features.users.services.UserServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserServiceInterface userService;
    public UserController(UserServiceInterface userService) {this.userService = userService;}

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    public ResponseEntity<List<UserResponse>> findAllUsers() {
        List<UserResponse> responseList = userService.findAll();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    public ResponseEntity<UserResponse> getUserByExternalId(@PathVariable UUID externalId) {
        UserResponse response = userService.findByExternalId(externalId);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{externalId}")
    @PreAuthorize("hasAuthority('DELETE_USERS')")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable UUID externalId) {
        userService.delete(externalId);
        return ResponseEntity.noContent().build();
    }
}
