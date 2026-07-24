package com.mahalak.media.dto.response;

import com.mahalak.media.enums.BlogStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BlogResponse {

    private String id;

    private String title;

    private String authorName;

    private String shortDescription;

    private String category;

    private List<String> tags;

    private LocalDate publishDate;

    private BlogStatus status;

    private Integer views;

    private String featuredImageUrl;

    private String contentFileUrl;

    private String contentFileId;

    private String authorImageUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}