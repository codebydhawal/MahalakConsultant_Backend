package com.mahalak.media.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRejectionRequest {

    @NotBlank(message = "Rejection reason is required")
    private String rejectionReason;
}
