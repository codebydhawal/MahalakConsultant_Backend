package com.mahalak.media.servicesImpl;

import com.mahalak.media.IServices.GoogleDriveService;
import com.mahalak.media.IServices.IMediaService;
import com.mahalak.media.dto.request.MediaRequest;
import com.mahalak.media.dto.response.MediaResponse;
import com.mahalak.media.dto.wrapper.FileUploadResponseDto;
import com.mahalak.media.entity.Media;
import com.mahalak.media.exception.BadRequestException;
import com.mahalak.media.exception.ResourceNotFoundException;
import com.mahalak.media.framework.GoogleEntityManager;
import com.mahalak.media.mapper.MediaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements IMediaService {

    private final GoogleEntityManager entityManager;

    private final MediaMapper mediaMapper;

    private final GoogleDriveService googleDriveService;

    @Override
    public MediaResponse addMedia(
            MediaRequest request,
            MultipartFile file) {

        if (request.getDisplayOrder() == null) {
            throw new BadRequestException("Display Order is required.");
        }

        boolean exists = entityManager.findAll(Media.class)
                .stream()
                .filter(media -> !Boolean.TRUE.equals(media.getIsMediaDeleted()))
                .anyMatch(media ->
                        request.getDisplayOrder().equals(media.getDisplayOrder()));

        if (exists) {
            throw new BadRequestException(
                    "Display Order " + request.getDisplayOrder() + " already exists."
            );
        }

        Media entity = mediaMapper.toEntity(request);

        handleThumbnail(entity, file);

        entity.setIsMediaDeleted(false);

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        entityManager.save(entity);

        return mediaMapper.toResponse(entity);
    }

    @Override
    public MediaResponse getMediaById(String mediaId) {

        Media media = entityManager.findById(Media.class, mediaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Media not found with id : " + mediaId));

        return mediaMapper.toResponse(media);
    }

    @Override
    public List<MediaResponse> getAllMedia() {

        return entityManager.findAll(Media.class)
                .stream()
                .filter(media ->
                        !Boolean.TRUE.equals(media.getIsMediaDeleted()))
                .sorted(
                        java.util.Comparator.comparing(
                                Media::getDisplayOrder,
                                java.util.Comparator.nullsLast(Integer::compareTo)
                        )
                )
                .map(mediaMapper::toResponse)
                .toList();
    }

    @Override
    public MediaResponse updateMedia(
            String mediaId,
            MediaRequest request,
            MultipartFile file) {

        Media media = entityManager.findById(Media.class, mediaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Media not found with id : " + mediaId));

        media = mediaMapper.updateEntity(request, media);

        handleThumbnail(media, file);

        media.setUpdatedAt(LocalDateTime.now());

        entityManager.update(media);

        return mediaMapper.toResponse(media);
    }

    @Override
    public MediaResponse deleteMedia(String mediaId) {

        Media media = entityManager.findById(Media.class, mediaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Media not found with id : " + mediaId));

        media.setIsMediaDeleted(true);
        media.setUpdatedAt(LocalDateTime.now());

        entityManager.update(media);

        return mediaMapper.toResponse(media);
    }

    @Override
    public List<MediaResponse> searchMedia(String keyword) {

        String searchKeyword = keyword.toLowerCase().trim();

        return entityManager.findAll(Media.class)
                .stream()
                .filter(media ->
                        !Boolean.TRUE.equals(media.getIsMediaDeleted()))
                .filter(media ->

                        (media.getTitle() != null &&
                                media.getTitle()
                                        .toLowerCase()
                                        .contains(searchKeyword))

                                ||

                                (media.getVideoUrl() != null &&
                                        media.getVideoUrl()
                                                .toLowerCase()
                                                .contains(searchKeyword)))
                .map(mediaMapper::toResponse)
                .toList();
    }

    /**
     * Upload Thumbnail Image
     */
    private void handleThumbnail(
            Media media,
            MultipartFile thumbnail) {

        if (thumbnail != null &&
                !thumbnail.isEmpty()) {

            String originalName =
                    thumbnail.getOriginalFilename();

            String extension = "";

            if (originalName != null &&
                    originalName.contains(".")) {

                extension =
                        originalName.substring(
                                originalName.lastIndexOf("."));
            }

            String generatedFileName =
                    "Media_Thumbnail_" +
                            UUID.randomUUID() +
                            extension;

            FileUploadResponseDto driveFile =
                    googleDriveService.upload(
                            thumbnail,
                            generatedFileName);

            media.setThumbnailImageName(generatedFileName);

            media.setThumbnailImageFileId(
                    driveFile.getFileId());

            media.setThumbnailImageUrl(
                    driveFile.getDownloadUrl());
        }
    }
}