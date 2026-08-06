package com.mahalak.media.mapper;

import com.mahalak.media.dto.request.TeamRequest;
import com.mahalak.media.dto.response.TeamResponse;
import com.mahalak.media.entity.Team;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    /**
     * Request -> Entity
     */
    @Mapping(target = "teamId", ignore = true)
    @Mapping(target = "profileImageName", ignore = true)
    @Mapping(target = "profileImageUrl", ignore = true)
    @Mapping(target = "profileImageFileId", ignore = true)
    @Mapping(target = "isTeamDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Team toEntity(TeamRequest request);

    /**
     * Entity -> Response
     */
    TeamResponse toResponse(Team team);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "teamId", ignore = true)
    @Mapping(target = "profileImageName", ignore = true)
    @Mapping(target = "profileImageUrl", ignore = true)
    @Mapping(target = "profileImageFileId", ignore = true)
    @Mapping(target = "isTeamDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Team updateEntity(
            TeamRequest request,
            @MappingTarget Team team
    );
}
