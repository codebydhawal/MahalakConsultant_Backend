package com.mahalak.media.dto.request;

import com.mahalak.media.enums.ProjectCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectRequest {

    @NotBlank(message = "Project title is required.")
    private String title;

    @NotNull(message = "Project category is required.")
    private ProjectCategory category;

    @NotBlank(message = "Short description is required.")
    private String shortDescription;

    private String clientName;

    private String location;

    private String completionDate;

    private String projectArea;
}
