package com.mahalak.media.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamRequest {

    private String fullName;

    private String designation;

    private String shortBio;

    private String email;

    private String phoneNumber;

    private String linkedInUrl;

    private String instagramUrl;

    private String facebookUrl;

    private String twitterUrl;

    private Integer displayOrder;

}
