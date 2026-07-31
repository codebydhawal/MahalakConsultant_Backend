package com.mahalak.media.mapper;

import com.mahalak.media.dto.request.ProjectRequest;
import com.mahalak.media.dto.response.ProjectResponse;
import com.mahalak.media.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "projectId", ignore = true)

    // Thumbnail (MultipartFile upload se service me set hoga)
    @Mapping(target = "thumbnailName", ignore = true)
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "thumbnailFileId", ignore = true)

    // Document (MultipartFile upload se service me set hoga)
    @Mapping(target = "documentName", ignore = true)
    @Mapping(target = "documentUrl", ignore = true)
    @Mapping(target = "documentFileId", ignore = true)

    // Backend managed fields
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "isProjectDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Project toEntity(ProjectRequest request);


    ProjectResponse toResponse(Project project);


    @Mapping(target = "projectId", ignore = true)

    // Existing uploaded files ko overwrite nahi karna
    @Mapping(target = "thumbnailName", ignore = true)
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "thumbnailFileId", ignore = true)

    @Mapping(target = "documentName", ignore = true)
    @Mapping(target = "documentUrl", ignore = true)
    @Mapping(target = "documentFileId", ignore = true)

    // Backend managed fields
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "isProjectDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ProjectRequest request,
                      @MappingTarget Project project);
}
