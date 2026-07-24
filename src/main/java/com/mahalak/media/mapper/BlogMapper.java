package com.mahalak.media.mapper;

import com.mahalak.media.dto.request.BlogRequest;
import com.mahalak.media.dto.response.BlogResponse;
import com.mahalak.media.entity.Blog;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BlogMapper {

    /**
     * Converts BlogRequest to Blog entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "featuredImageName", ignore = true)
    @Mapping(target = "featuredImageUrl", ignore = true)
    @Mapping(target = "featuredImageFileId", ignore = true)
    @Mapping(target = "contentFileName", ignore = true)
    @Mapping(target = "contentFileUrl", ignore = true)
    @Mapping(target = "contentFileId", ignore = true)
    @Mapping(target = "authorImageName", ignore = true)
    @Mapping(target = "authorImageUrl", ignore = true)
    @Mapping(target = "authorImageFileId", ignore = true)
    @Mapping(target = "isBlogDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Blog toEntity(BlogRequest request);

    /**
     * Converts Blog entity to BlogResponse.
     */
    BlogResponse toResponse(Blog blog);

    /**
     * Updates an existing Blog entity with non-null values
     * from BlogRequest.
     */
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    void updateEntity(BlogRequest request, @MappingTarget Blog blog);

}