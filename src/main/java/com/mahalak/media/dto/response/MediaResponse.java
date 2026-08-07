package com.mahalak.media.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponse {

    private String mediaId;

    private String title;

    private String videoUrl;

    private String thumbnailImageName;

    private String thumbnailImageUrl;

    private String thumbnailImageFileId;

    private Integer displayOrder;

    private Boolean isMediaDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
