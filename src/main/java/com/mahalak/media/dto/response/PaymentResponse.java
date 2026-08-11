package com.mahalak.media.dto.response;

import com.mahalak.media.enums.PaymentMethod;
import com.mahalak.media.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private String paymentId;
    private String orderId;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private Double amount;
    private String transactionId;
    private String screenshotName;
    private String screenshotUrl;
    private String verifiedBy;
    private LocalDateTime verifiedAt;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
