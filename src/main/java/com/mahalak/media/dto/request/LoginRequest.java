package com.mahalak.media.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class LoginRequest {

    private String email;
    private String password;

}

