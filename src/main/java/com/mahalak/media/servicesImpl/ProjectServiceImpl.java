package com.mahalak.media.servicesImpl;

import com.mahalak.media.IServices.GoogleDriveService;
import com.mahalak.media.IServices.IProjectService;
import com.mahalak.media.dto.request.ProjectRequest;
import com.mahalak.media.dto.response.ProductResponse;
import com.mahalak.media.dto.response.ProjectDocumentResponse;
import com.mahalak.media.dto.response.ProjectResponse;
import com.mahalak.media.dto.wrapper.DownloadResponse;
import com.mahalak.media.dto.wrapper.FileUploadResponseDto;
import com.mahalak.media.entity.Product;
import com.mahalak.media.entity.Project;
import com.mahalak.media.exception.BadRequestException;
import com.mahalak.media.exception.ResourceNotFoundException;
import com.mahalak.media.framework.GoogleEntityManager;
import com.mahalak.media.mapper.ProductMapper;
import com.mahalak.media.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zwobble.mammoth.DocumentConverter;
import org.zwobble.mammoth.Result;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements IProjectService {

    private final GoogleEntityManager entityManager;

    private final ProjectMapper projectMapper;

    private final GoogleDriveService googleDriveService;

    @Override
    public ProjectResponse addProject(ProjectRequest request, MultipartFile file, MultipartFile document) {

        Project entity = projectMapper.toEntity(request);

        handleThumbnailImage(entity, file);
        handleContentFile(entity, document);

        entity.setViews(0L);
        entity.setIsProjectDeleted(false);

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        entityManager.save(entity);

        return projectMapper.toResponse(entity);
    }

    @Override
    public ProjectResponse getProjectById(String projectId) {
        Project project = entityManager.findById(Project.class, projectId).orElseThrow(() -> new RuntimeException("project not found with id : " + projectId));

        return projectMapper.toResponse(project);
    }

    @Override
    public List<ProjectResponse> getAllProjects() {

        return entityManager.findAll(Project.class).stream().filter(project -> !Boolean.TRUE.equals(project.getIsProjectDeleted())).map(projectMapper::toResponse).toList();
    }

    @Override
    public ProjectResponse updateProject(String projectId, ProjectRequest request, MultipartFile file, MultipartFile document) {
        Project project = entityManager.findById(Project.class, projectId).orElseThrow(() -> new RuntimeException("project not found with id : " + projectId));

        if (request == null && (file == null || file.isEmpty()) && (document == null || document.isEmpty())) {
            throw new BadRequestException("Provide project details, a thumbnail, or a document to update.");
        }

        if (request != null) {
            projectMapper.updateEntity(request, project);
        }

        String oldThumbnailFileId = project.getThumbnailFileId();
        String oldDocumentFileId = project.getDocumentFileId();

        handleThumbnailImage(project, file);
        handleContentFile(project, document);

        String newThumbnailFileId = project.getThumbnailFileId();
        String newDocumentFileId = project.getDocumentFileId();

        project.setUpdatedAt(LocalDateTime.now());

        try {
            entityManager.update(project);
        } catch (Exception exception) {
            deleteDriveFileQuietly(newThumbnailFileId, oldThumbnailFileId);
            deleteDriveFileQuietly(newDocumentFileId, oldDocumentFileId);
            throw exception;
        }

        deleteDriveFileQuietly(oldThumbnailFileId, newThumbnailFileId);
        deleteDriveFileQuietly(oldDocumentFileId, newDocumentFileId);

        return projectMapper.toResponse(project);
    }

    @Override
    public ProjectResponse deleteProject(String projectId) {
        Project project = entityManager.findById(Project.class, projectId).orElseThrow(() -> new RuntimeException("project not found with id : " + projectId));

        project.setIsProjectDeleted(true);
        project.setUpdatedAt(LocalDateTime.now());

        entityManager.update(project);

        return projectMapper.toResponse(project);
    }

    @Override
    public List<ProjectResponse> searchProjects(String keyword) {
        String searchKeyword = keyword.toLowerCase().trim();

        return entityManager.findAll(Project.class).stream().filter(project -> !Boolean.TRUE.equals(project.getIsProjectDeleted())).filter(project -> (project.getTitle() != null && project.getTitle().toLowerCase().contains(searchKeyword))).map(projectMapper::toResponse).toList();
    }

    @Override
    public ProjectDocumentResponse getProjectDocumentContent(String projectId) throws Exception {

        Project project = entityManager.findById(Project.class, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        DownloadResponse response =
                googleDriveService.download(project.getDocumentFileId());

        try (InputStream inputStream =
                     new ByteArrayInputStream(response.getData())) {

            DocumentConverter converter = new DocumentConverter();

            Result<String> result = converter.convertToHtml(inputStream);

            return new ProjectDocumentResponse(result.getValue());
        }
    }

    @Override
    public List<ProjectResponse> getRandomProjects() {

        List<Project> projects = entityManager.findAll(Project.class)
                .stream()
                .filter(project ->
                        !Boolean.TRUE.equals(project.getIsProjectDeleted()))
                .toList();

        if (projects.isEmpty()) {
            return Collections.emptyList();
        }

        Random random = new Random();
        Set<Integer> indexes = new HashSet<>();

        while (indexes.size() < Math.min(5, projects.size())) {
            indexes.add(random.nextInt(projects.size()));
        }

        return indexes.stream()
                .map(projects::get)
                .map(ProjectMapper.INSTANCE::toResponse)
                .collect(Collectors.toList());
    }

    private void handleThumbnailImage(Project project, MultipartFile thumbnailImage) {
        if (thumbnailImage != null && !thumbnailImage.isEmpty()) {

            String originalName = thumbnailImage.getOriginalFilename();
            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            String generatedFileName = "Project_Thumbnail_" + UUID.randomUUID() + extension;

            FileUploadResponseDto driveFile = googleDriveService.upload(thumbnailImage, generatedFileName);

            project.setThumbnailName(generatedFileName);
            project.setThumbnailFileId(driveFile.getFileId());
            project.setThumbnailUrl(driveFile.getDownloadUrl());

        }
    }

    private void handleContentFile(Project project, MultipartFile document) {
        if (document != null && !document.isEmpty()) {

            String originalName = document.getOriginalFilename();
            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            String generatedFileName = "Project_Desc_" + originalName;

            FileUploadResponseDto driveFile = googleDriveService.upload(document, generatedFileName);

            project.setDocumentName(generatedFileName);
            project.setDocumentFileId(driveFile.getFileId());
            project.setDocumentUrl(driveFile.getDownloadUrl());

        }
    }

    private void deleteDriveFileQuietly(String fileId, String retainedFileId) {
        if (fileId == null || fileId.isBlank() || fileId.equals(retainedFileId)) {
            return;
        }
        try {
            googleDriveService.delete(fileId);
        } catch (Exception ignored) {
            // The saved entity already references the replacement file.
        }
    }
}
