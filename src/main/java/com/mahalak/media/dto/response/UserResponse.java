package com.mahalak.media.dto.response;

import com.mahalak.media.enums.UserStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String fullName;

    private String email;

    private UserStatus status;

    private String role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
