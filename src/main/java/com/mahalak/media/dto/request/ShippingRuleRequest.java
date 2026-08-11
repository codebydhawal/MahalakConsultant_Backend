package com.mahalak.media.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingRuleRequest {

    @NotBlank(message = "Shipping rule name is required.")
    private String name;

    @NotNull(message = "Shipping rate is required.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Shipping rate cannot be negative.")
    private Double rate;

    @NotNull(message = "Active status is required.")
    private Boolean active;

    @NotNull(message = "Start date is required.")
    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
