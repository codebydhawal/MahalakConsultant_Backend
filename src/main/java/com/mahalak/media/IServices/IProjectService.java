package com.mahalak.media.IServices;

import com.mahalak.media.dto.request.ProjectRequest;
import com.mahalak.media.dto.response.ProjectDocumentResponse;
import com.mahalak.media.dto.response.ProjectResponse;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProjectService {

    ProjectResponse addProject(@Valid ProjectRequest request, MultipartFile file, MultipartFile document);

    ProjectResponse getProjectById(String projectId);

    List<ProjectResponse> getAllProjects();

    ProjectResponse updateProject(String projectId, @Valid ProjectRequest request, MultipartFile file, MultipartFile document);

    ProjectResponse deleteProject(String projectId);

    List<ProjectResponse> searchProjects(String keyword);

    ProjectDocumentResponse getProjectDocumentContent(String projectId) throws Exception;

    List<ProjectResponse> getRandomProjects();
}
