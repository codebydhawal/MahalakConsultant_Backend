package com.mahalak.media.dto.response;

import com.mahalak.media.entity.Role;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class LoginResponse {

    private String email;
    private String token;

}

