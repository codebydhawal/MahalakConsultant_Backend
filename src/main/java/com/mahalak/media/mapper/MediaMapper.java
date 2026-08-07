package com.mahalak.media.mapper;

import com.mahalak.media.dto.request.MediaRequest;
import com.mahalak.media.dto.response.MediaResponse;
import com.mahalak.media.entity.Media;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MediaMapper {

    /**
     * Request -> Entity
     */
    Media toEntity(MediaRequest request);

    /**
     * Entity -> Response
     */
    MediaResponse toResponse(Media media);

    /**
     * Update Existing Entity
     */
    Media updateEntity(MediaRequest request, @MappingTarget Media media);
}
