package com.mahalak.media.IServices;

import com.mahalak.media.dto.request.LoginRequest;
import com.mahalak.media.dto.request.RegisterUserRequest;
import com.mahalak.media.dto.request.UpdateUserRequest;
import com.mahalak.media.dto.response.LoginResponse;
import com.mahalak.media.dto.response.UserResponse;
import jakarta.validation.Valid;
import org.apache.http.auth.InvalidCredentialsException;

import java.util.List;

public interface AuthService {
    UserResponse register(@Valid RegisterUserRequest request);

    LoginResponse login(@Valid LoginRequest request) throws InvalidCredentialsException;

    UserResponse getUserById(Long userId);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long userId, @Valid UpdateUserRequest request);

    UserResponse deleteUser(Long userId);

    UserResponse updateUserRole(Long userId, String role);

//
//    ResponseEntity<LogInResponse> login(LogInRequest logInRequest, HttpServletRequest request);
//
//    ResponseEntity<ResetPasswordResponse> resetPassword(ResetPasswordRequest resetPasswordRequest, HttpServletRequest request);
//
//    OTP saveOtp(String email);
//
//    ResponseEntity<ResendOTPResponse> resendOTP(String email);
//
//    ResponseEntity<OTPVerificationResponse> verifyOTP(OTPVerificationRequest  otpVerificationRequest);
//
//    ResponseEntity<ResetPasswordResponse> sendPasswordResetOTP(PasswordResetOTPRequest passwordResetOTPRequest);
}
