package com.mahalak.media.dto.request;

import com.mahalak.media.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RegisterUserRequest implements Serializable {

    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String role;

}
