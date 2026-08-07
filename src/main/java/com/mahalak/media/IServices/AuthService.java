package com.mahalak.media.IServices;

import com.mahalak.media.dto.request.BlogRequest;
import com.mahalak.media.dto.request.LoginRequest;
import com.mahalak.media.dto.request.RegisterUserRequest;
import com.mahalak.media.dto.request.UpdateUserRequest;
import com.mahalak.media.dto.response.LoginResponse;
import com.mahalak.media.dto.response.UserResponse;
import jakarta.validation.Valid;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AuthService {
    UserResponse register(@Valid RegisterUserRequest request);

    LoginResponse login(@Valid LoginRequest request) throws InvalidCredentialsException;

    UserResponse getUserById(Long userId);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long userId, @Valid UpdateUserRequest request,MultipartFile profileImage);

    UserResponse deleteUser(Long userId);

    UserResponse updateUserRoleAndStatus(Long userId, String role,String status);
}
