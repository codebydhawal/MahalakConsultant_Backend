package com.mahalak.media.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Product name is required.")
    private String name;

    @NotBlank(message = "Description is required.")
    private String description;

    @NotBlank(message = "Category is required.")
    private String category;

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0.")
    private Double price;

    @NotNull(message = "Stock is required.")
    @Min(value = 0, message = "Stock cannot be negative.")
    private Integer stock;

    @NotBlank(message = "Status is required.")
    private String status;
}
