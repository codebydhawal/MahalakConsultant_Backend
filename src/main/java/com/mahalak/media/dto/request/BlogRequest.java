package com.mahalak.media.dto.request;

import com.mahalak.media.enums.BlogStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BlogRequest {

    @NotBlank(message = "Blog title is required.")
    private String title;

    @NotBlank(message = "Author name is required.")
    private String authorName;

    @NotBlank(message = "Short description is required.")
    private String shortDescription;

    @NotBlank(message = "Category is required.")
    private String category;

    private List<String> tags;

    @NotNull(message = "Publish date is required.")
    private LocalDate publishDate;

    @NotNull(message = "Blog status is required.")
    private BlogStatus status;
}