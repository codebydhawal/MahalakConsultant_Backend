package com.mahalak.media.IServices;

import com.mahalak.media.dto.request.MediaRequest;
import com.mahalak.media.dto.response.MediaResponse;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IMediaService {
    MediaResponse addMedia(@Valid MediaRequest request, MultipartFile file);

    MediaResponse getMediaById(String mediaId);

    List<MediaResponse> getAllMedia();

    MediaResponse updateMedia(String mediaId, @Valid MediaRequest request, MultipartFile file);

    MediaResponse deleteMedia(String mediaId);

    List<MediaResponse> searchMedia(String keyword);
}
