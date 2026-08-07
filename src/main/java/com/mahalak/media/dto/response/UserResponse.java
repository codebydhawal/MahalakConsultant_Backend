package com.mahalak.media.dto.response;

import com.mahalak.media.enums.UserStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String role;

    private UserStatus status;

    private String profileImageName;

    private String profileImageUrl;

    private List<AddressResponse> addresses;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}