package com.mahalak.media.dto.request;
import lombok.*; import java.time.LocalDateTime;
@Data @NoArgsConstructor @AllArgsConstructor public class CouponRequest { private String code; private Double discountPercent; private Double minOrderAmount; private Double maxDiscountAmount; private Integer maxUsesPerUser; private Boolean active; private LocalDateTime startDate; private LocalDateTime endDate; }
