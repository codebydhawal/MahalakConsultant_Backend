package com.mahalak.media.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private String productId;

    private String name;

    private String description;

    private String category;

    private Double price;

    private Integer stock;

    private String status;

    private Boolean isProductDeleted;

    // Image Details
    private String imageName;

    private String imageUrl;

    private String imageFileId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
