package com.mahalak.media.controller;

import com.mahalak.media.IServices.ITeamService;
import com.mahalak.media.dto.request.TeamRequest;
import com.mahalak.media.dto.response.TeamResponse;
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
@RequestMapping("/rest/team")
@RequiredArgsConstructor
public class TeamController {

    private final ITeamService teamService;

    /**
     * Add Team Member
     */
    @PostMapping(
            value = "/add",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<TeamResponse>> addTeam(

            @Valid @RequestPart("request") TeamRequest request,

            @RequestPart("file") MultipartFile file) {

        TeamResponse response =
                teamService.addTeam(request, file);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "Team member added successfully.",
                        response));
    }

    /**
     * Get Team Member By Id
     */
    @GetMapping("/get")
    public ResponseEntity<ApiResponse<TeamResponse>> getTeamById(

            @RequestParam String teamId) {

        TeamResponse response =
                teamService.getTeamById(teamId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Team member fetched successfully.",
                        response));
    }

    /**
     * Get All Team Members
     */
    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<List<TeamResponse>>> getAllTeams() {

        List<TeamResponse> response =
                teamService.getAllTeams();

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Team members fetched successfully.",
                        response));
    }

    /**
     * Update Team Member
     */
    @PutMapping(
            value = "/update",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<TeamResponse>> updateTeam(

            @RequestParam String teamId,

            @Valid @RequestPart(value = "request", required = false) TeamRequest request,

            @RequestPart(value = "file", required = false)
            MultipartFile file) {

        TeamResponse response =
                teamService.updateTeam(teamId, request, file);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Team member updated successfully.",
                        response));
    }

    /**
     * Delete Team Member
     */
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<TeamResponse>> deleteTeam(
            @RequestParam String teamId) {

        TeamResponse response = teamService.deleteTeam(teamId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Team member deleted successfully.",
                        response));
    }

    /**
     * Search Team Members
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TeamResponse>>> searchTeam(
            @RequestParam String keyword) {

        List<TeamResponse> response =
                teamService.searchTeam(keyword);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Team members fetched successfully.",
                        response));
    }

}
