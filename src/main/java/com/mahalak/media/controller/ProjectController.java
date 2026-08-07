package com.mahalak.media.controller;

import com.mahalak.media.IServices.IProjectService;
import com.mahalak.media.dto.request.ProjectRequest;
import com.mahalak.media.dto.response.ProductResponse;
import com.mahalak.media.dto.response.ProjectDocumentResponse;
import com.mahalak.media.dto.response.ProjectResponse;
import com.mahalak.media.dto.wrapper.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/rest/project")
@RequiredArgsConstructor
public class ProjectController {

    private final IProjectService projectService;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProjectResponse>> addProject(
            @Valid @RequestPart("request") ProjectRequest request,
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "document", required = false) MultipartFile document) {

        ProjectResponse response = projectService.addProject(request, file, document);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "Project added successfully.",
                        response));
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(
            @RequestParam String projectId) {

        ProjectResponse response = projectService.getProjectById(projectId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Project fetched successfully.",
                        response));
    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getAllProjects() {

        List<ProjectResponse> response = projectService.getAllProjects();

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Projects fetched successfully.",
                        response));
    }

    @PatchMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @RequestParam String projectId,
            @Valid @RequestPart("request") ProjectRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "document", required = false) MultipartFile document) {

        ProjectResponse response = projectService.updateProject(
                projectId,
                request,
                file,
                document);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Project updated successfully.",
                        response));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<ProjectResponse>> deleteProject(
            @RequestParam String projectId) {

        ProjectResponse response = projectService.deleteProject(projectId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Project deleted successfully.",
                        response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> searchProjects(
            @RequestParam String keyword) {

        List<ProjectResponse> response = projectService.searchProjects(keyword);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Projects fetched successfully.",
                        response));
    }

    @GetMapping("/document-content")
    public ResponseEntity<ProjectDocumentResponse> getDocumentContent(
            @RequestParam String projectId) throws Exception {

        return ResponseEntity.ok(
                projectService.getProjectDocumentContent(projectId)
        );
    }

    @GetMapping("/random")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getRandomProjects() {

      List<ProjectResponse> projectResponseList=  projectService.getRandomProjects();

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Random projects fetched successfully.",
                projectResponseList
        ));
    }
}