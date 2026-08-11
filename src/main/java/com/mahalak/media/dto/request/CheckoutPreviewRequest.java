package com.mahalak.media.dto.request;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
@Data @NoArgsConstructor @AllArgsConstructor
public class CheckoutPreviewRequest { @NotBlank private String addressId; private String couponCode; }
