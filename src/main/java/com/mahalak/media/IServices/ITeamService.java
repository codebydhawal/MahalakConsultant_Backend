package com.mahalak.media.IServices;

import com.mahalak.media.dto.request.TeamRequest;
import com.mahalak.media.dto.response.TeamResponse;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ITeamService {
    TeamResponse addTeam(@Valid TeamRequest request, MultipartFile file);

    TeamResponse getTeamById(String teamId);

    List<TeamResponse> getAllTeams();

    TeamResponse updateTeam(String teamId, @Valid TeamRequest request, MultipartFile file);

    TeamResponse deleteTeam(String teamId);

    List<TeamResponse> searchTeam(String keyword);
}
