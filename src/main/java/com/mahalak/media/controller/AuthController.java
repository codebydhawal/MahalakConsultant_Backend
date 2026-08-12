package com.mahalak.media.controller;

import com.mahalak.media.IServices.AuthService;
import com.mahalak.media.dto.request.BlogRequest;
import com.mahalak.media.dto.request.LoginRequest;
import com.mahalak.media.dto.request.UpdateUserRequest;
import com.mahalak.media.dto.response.LoginResponse;
import com.mahalak.media.dto.response.UserResponse;
import com.mahalak.media.dto.wrapper.ApiResponse;
import com.mahalak.media.dto.request.RegisterUserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/rest/auth")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AuthController {

    private final AuthService authService;

    /**
     * Register User
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(
            @Valid @RequestBody RegisterUserRequest request) {

        UserResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "User registered successfully.",
                        response));
    }

    /**
     * Login User
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) throws InvalidCredentialsException {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Login successful.",
                        response));
    }

    /**
     * Get User By Id
     */
    @GetMapping("/get")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @RequestParam Long userId) {

        UserResponse response = authService.getUserById(userId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "User fetched successfully.",
                        response));
    }

    /**
     * Get All Users
     */
    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        List<UserResponse> response = authService.getAllUsers();

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "User fetched successfully.",
                        response));
    }

    /**
     * Update User
     */
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @RequestParam Long userId,
            @Valid @RequestPart(value = "request", required = false) UpdateUserRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        UserResponse response = authService.updateUser(userId, request,profileImage);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "User update successfully.",
                        response));
    }

    /**
     * Delete User
     */
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<UserResponse>> deleteUser(
            @RequestParam Long userId) {

        UserResponse response = authService.deleteUser(userId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "User deleted successfully.",
                        response
                )
        );
    }

    @PatchMapping("/update-role-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRoleAndStatus(
            @RequestParam Long userId, @RequestParam String role, @RequestParam String status) {

        UserResponse response = authService.updateUserRoleAndStatus(userId, role, status);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "User update role successfully.",
                        response));
    }
}