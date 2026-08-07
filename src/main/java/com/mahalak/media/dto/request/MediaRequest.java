package com.mahalak.media.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaRequest {

    private String title;

    private String videoUrl;

    private Integer displayOrder;

}
