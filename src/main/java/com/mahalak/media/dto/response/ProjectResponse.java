package com.mahalak.media.dto.response;

import com.mahalak.media.enums.ProjectCategory;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectResponse {

    private String projectId;

    private String title;

    private ProjectCategory category;

    private String shortDescription;

    private String thumbnailName;

    private String thumbnailUrl;

    private String thumbnailFileId;

    private String documentName;

    private String documentUrl;

    private String documentFileId;

    private String clientName;

    private String location;

    private String completionDate;

    private String projectArea;

    private Long views;

    private Boolean isProjectDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}