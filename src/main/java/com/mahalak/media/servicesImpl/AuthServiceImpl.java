package com.mahalak.media.servicesImpl;

import com.mahalak.media.IServices.AuthService;
import com.mahalak.media.auth.JwtUtil;
import com.mahalak.media.dto.request.LoginRequest;
import com.mahalak.media.dto.request.RegisterUserRequest;
import com.mahalak.media.dto.request.UpdateUserRequest;
import com.mahalak.media.dto.response.LoginResponse;
import com.mahalak.media.dto.response.UserResponse;
import com.mahalak.media.entity.Role;
import com.mahalak.media.entity.User;
import com.mahalak.media.enums.UserStatus;
import com.mahalak.media.exception.BadRequestException;
import com.mahalak.media.exception.DuplicateResourceException;
import com.mahalak.media.exception.ResourceNotFoundException;
import com.mahalak.media.mapper.UserMapper;
import com.mahalak.media.repository.RoleRepository;
import com.mahalak.media.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserResponse register(RegisterUserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("user with this Email already registered.");
        }

        Role role = roleRepository.findByRole(request.getRole()).orElseThrow(() -> new ResourceNotFoundException("Default role not found."));

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) throws InvalidCredentialsException {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new InvalidCredentialsException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password.");
        }

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new BadRequestException("Your account has been deactivated. Please contact the administrator.");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return LoginResponse.builder().token(token).email(user.getEmail()).build();
    }

    @Override
    public UserResponse getUserById(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + userId));

        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {

        List<User> users = userRepository.findAll();

        return userMapper.toResponseList(users);
    }

    @Override
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {

            throw new DuplicateResourceException("Email already exists.");
        }

        userMapper.updateUser(request, user);

        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse deleteUser(Long userId) {

        log.info("Soft deleting user with id : {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + userId));

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new BadRequestException("User is already inactive.");
        }

        user.setStatus(UserStatus.INACTIVE);

        User updatedUser = userRepository.save(user);

        log.info("User {} marked as INACTIVE.", userId);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    public UserResponse updateUserRoleAndStatus(Long userId, String role, String status) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found."));

        Role roleEntity = roleRepository.findByRole(role).orElseThrow(() -> new ResourceNotFoundException("Role not found."));

        UserStatus userStatus = UserStatus.valueOf(status.toUpperCase());

        boolean sameRole = user.getRole().getRole().equalsIgnoreCase(roleEntity.getRole());
        boolean sameStatus = user.getStatus() == userStatus;

        // Throw only if nothing changed
        if (sameRole && sameStatus) {
            throw new BadRequestException("No changes detected.");
        }

        // Update only the changed fields
        if (!sameRole) {
            user.setRole(roleEntity);
        }

        if (!sameStatus) {
            user.setStatus(userStatus);
        }

        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }
}

