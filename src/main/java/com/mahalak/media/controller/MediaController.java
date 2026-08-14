package com.mahalak.media.controller;

import com.mahalak.media.IServices.IMediaService;
import com.mahalak.media.dto.request.MediaRequest;
import com.mahalak.media.dto.response.MediaResponse;
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
@RequestMapping("/rest/media")
@RequiredArgsConstructor
public class MediaController {

    private final IMediaService mediaService;

    /**
     * Add Media
     */
    @PostMapping(
            value = "/add",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<MediaResponse>> addMedia(

            @Valid @RequestPart("request") MediaRequest request,

            @RequestPart("file") MultipartFile file) {

        MediaResponse response =
                mediaService.addMedia(request, file);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "Media added successfully.",
                        response));
    }

    /**
     * Get Media By Id
     */
    @GetMapping("/get")
    public ResponseEntity<ApiResponse<MediaResponse>> getMediaById(

            @RequestParam String mediaId) {

        MediaResponse response =
                mediaService.getMediaById(mediaId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Media fetched successfully.",
                        response));
    }

    /**
     * Get All Media
     */
    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<List<MediaResponse>>> getAllMedia() {

        List<MediaResponse> response =
                mediaService.getAllMedia();

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Media fetched successfully.",
                        response));
    }

    /**
     * Update Media
     */
    @PutMapping(
            value = "/update",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<MediaResponse>> updateMedia(

            @RequestParam String mediaId,

            @Valid @RequestPart(value = "request", required = false) MediaRequest request,

            @RequestPart(value = "file", required = false)
            MultipartFile file) {

        MediaResponse response =
                mediaService.updateMedia(mediaId, request, file);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Media updated successfully.",
                        response));
    }

    /**
     * Delete Media
     */
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<MediaResponse>> deleteMedia(

            @RequestParam String mediaId) {

        MediaResponse response =
                mediaService.deleteMedia(mediaId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Media deleted successfully.",
                        response));
    }

    /**
     * Search Media
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MediaResponse>>> searchMedia(

            @RequestParam String keyword) {

        List<MediaResponse> response =
                mediaService.searchMedia(keyword);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Media fetched successfully.",
                        response));
    }

}
