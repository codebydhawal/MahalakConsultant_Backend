package com.mahalak.media.dto.request;
import lombok.*; import java.time.LocalDateTime;
@Data @NoArgsConstructor @AllArgsConstructor public class ShipmentRequest { private String courierName; private String trackingNumber; private String trackingUrl; private LocalDateTime estimatedDelivery; }
