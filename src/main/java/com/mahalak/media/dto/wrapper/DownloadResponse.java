package com.mahalak.media.dto.wrapper;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class DownloadResponse {

    private byte[] data;
    private String mimeType;
}
