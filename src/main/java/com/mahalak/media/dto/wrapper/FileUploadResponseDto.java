package com.mahalak.media.dto.wrapper;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadResponseDto {

    private String fileId;

    private String fileName;

    private String mimeType;

    private String downloadUrl;

    private Long size;

}