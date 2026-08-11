package com.mahalak.media.dto.response;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CheckoutPreviewResponse { private Double productTotal; private Double taxAmount; private Double ruleDiscountAmount; private Double couponDiscountAmount; private Double shippingAmount; private Double finalAmount; }
