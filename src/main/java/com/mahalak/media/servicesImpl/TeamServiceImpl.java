package com.mahalak.media.servicesImpl;

import com.mahalak.media.IServices.GoogleDriveService;
import com.mahalak.media.IServices.ITeamService;
import com.mahalak.media.dto.request.TeamRequest;
import com.mahalak.media.dto.response.TeamResponse;
import com.mahalak.media.dto.wrapper.FileUploadResponseDto;
import com.mahalak.media.entity.Team;
import com.mahalak.media.exception.BadRequestException;
import com.mahalak.media.exception.ResourceNotFoundException;
import com.mahalak.media.framework.GoogleEntityManager;
import com.mahalak.media.mapper.TeamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements ITeamService {

    private final GoogleEntityManager entityManager;

    private final TeamMapper teamMapper;

    private final GoogleDriveService googleDriveService;

    @Override
    public TeamResponse addTeam(
            TeamRequest request,
            MultipartFile file) {

        if (request.getDisplayOrder() == null) {
            throw new BadRequestException("Display Order is required.");
        }

        boolean exists = entityManager.findAll(Team.class)
                .stream()
                .filter(team -> !Boolean.TRUE.equals(team.getIsTeamDeleted()))
                .anyMatch(team ->
                        request.getDisplayOrder().equals(team.getDisplayOrder()));

        if (exists) {
            throw new BadRequestException(
                    "Display Order " + request.getDisplayOrder() + " already exists."
            );
        }

        Team entity = teamMapper.toEntity(request);

        handleProfileImage(entity, file);

        entity.setIsTeamDeleted(false);

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        entityManager.save(entity);

        return teamMapper.toResponse(entity);
    }

    @Override
    public TeamResponse getTeamById(String teamId) {

        Team team = entityManager.findById(Team.class, teamId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team member not found with id : " + teamId));

        return teamMapper.toResponse(team);
    }

    @Override
    public List<TeamResponse> getAllTeams() {

        return entityManager.findAll(Team.class)
                .stream()
                .filter(team ->
                        !Boolean.TRUE.equals(team.getIsTeamDeleted()))
                .sorted(
                        java.util.Comparator.comparing(
                                Team::getDisplayOrder,
                                java.util.Comparator.nullsLast(Integer::compareTo)
                        )
                )
                .map(teamMapper::toResponse)
                .toList();
    }

    @Override
    public TeamResponse updateTeam(
            String teamId,
            TeamRequest request,
            MultipartFile file) {

        Team team = entityManager.findById(Team.class, teamId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team member not found with id : " + teamId));

        team = teamMapper.updateEntity(request, team);

        handleProfileImage(team, file);

        team.setUpdatedAt(LocalDateTime.now());

        entityManager.update(team);

        return teamMapper.toResponse(team);
    }

    @Override
    public TeamResponse deleteTeam(String teamId) {

        Team team = entityManager.findById(Team.class, teamId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team member not found with id : " + teamId));

        team.setIsTeamDeleted(true);
        team.setUpdatedAt(LocalDateTime.now());

        entityManager.update(team);

        return teamMapper.toResponse(team);
    }

    @Override
    public List<TeamResponse> searchTeam(String keyword) {

        String searchKeyword = keyword.toLowerCase().trim();

        return entityManager.findAll(Team.class)
                .stream()
                .filter(team ->
                        !Boolean.TRUE.equals(team.getIsTeamDeleted()))
                .filter(team ->

                        (team.getFullName() != null &&
                                team.getFullName()
                                        .toLowerCase()
                                        .contains(searchKeyword))

                                ||

                                (team.getDesignation() != null &&
                                        team.getDesignation()
                                                .toLowerCase()
                                                .contains(searchKeyword)))
                .map(teamMapper::toResponse)
                .toList();
    }

    /**
     * Upload Profile Image
     */
    private void handleProfileImage(
            Team team,
            MultipartFile profileImage) {

        if (profileImage != null &&
                !profileImage.isEmpty()) {

            String originalName =
                    profileImage.getOriginalFilename();

            String extension = "";

            if (originalName != null &&
                    originalName.contains(".")) {

                extension =
                        originalName.substring(
                                originalName.lastIndexOf("."));
            }

            String generatedFileName =
                    "Team_Profile_" +
                            UUID.randomUUID() +
                            extension;

            FileUploadResponseDto driveFile =
                    googleDriveService.upload(
                            profileImage,
                            generatedFileName);

            team.setProfileImageName(generatedFileName);

            team.setProfileImageFileId(
                    driveFile.getFileId());

            team.setProfileImageUrl(
                    driveFile.getDownloadUrl());
        }
    }
}
