package com.mahalak.media.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponse {

    private String teamId;

    private String fullName;

    private String designation;

    private String shortBio;

    private String profileImageName;

    private String profileImageUrl;

    private String profileImageFileId;

    private String email;

    private String phoneNumber;

    private String linkedInUrl;

    private String instagramUrl;

    private String facebookUrl;

    private String twitterUrl;

    private Boolean isTeamDeleted;

    private Integer displayOrder;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}