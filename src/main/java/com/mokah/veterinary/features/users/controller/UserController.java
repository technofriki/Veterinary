package com.mokah.veterinary.features.users.controller;

import com.mokah.veterinary.features.users.dto.UserResponse;
import com.mokah.veterinary.features.users.dto.UserPublicResponse;
import com.mokah.veterinary.security.ProfessionalVisibility;
import com.mokah.veterinary.features.users.services.UserServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceInterface userService;
    public UserController(UserServiceInterface userService) {this.userService = userService;}

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    public ResponseEntity<List<?>> findAllUsers() {
        List<UserResponse> responseList = userService.findAll();
        if (ProfessionalVisibility.isAdmin()) return ResponseEntity.ok(responseList);
        return ResponseEntity.ok(responseList.stream().map(this::visibleUser).toList());
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    public ResponseEntity<?> getUserByExternalId(@PathVariable UUID externalId) {
        UserResponse response = userService.findByExternalId(externalId);
        return ResponseEntity.ok(visibleUser(response));
    }

    private Object visibleUser(UserResponse user) {
        if (ProfessionalVisibility.isAdmin() || ProfessionalVisibility.isOwnUser(user.getExternalId(), user.getEmail())) return user;
        return new UserPublicResponse(user.getExternalId(), user.getFirstName(), user.getLastName());
    }


    @DeleteMapping("/{externalId}")
    @PreAuthorize("hasAuthority('DELETE_USERS')")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable UUID externalId) {
        userService.delete(externalId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{externalId}/reactivate")
    public ResponseEntity<Void> reactivateUser(@PathVariable UUID externalId) {
        userService.reactivate(externalId);
        return ResponseEntity.noContent().build();
    }
}
