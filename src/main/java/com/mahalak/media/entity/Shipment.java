package com.mahalak.media.entity;

import com.mahalak.media.annotations.Sheet;
import com.mahalak.media.annotations.SheetColumn;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Sheet(name = "Shipment")
public class Shipment {
    @SheetColumn(name = "Shipment_Id", id = true, prefix = "SHP", order = 1) private String shipmentId;
    @SheetColumn(name = "Order_Id", order = 2) private String orderId;
    @SheetColumn(name = "Courier_Name", order = 3) private String courierName;
    @SheetColumn(name = "Tracking_Number", order = 4) private String trackingNumber;
    @SheetColumn(name = "Tracking_Url", order = 5) private String trackingUrl;
    @SheetColumn(name = "Estimated_Delivery", order = 6) private LocalDateTime estimatedDelivery;
    @SheetColumn(name = "Shipped_At", order = 7) private LocalDateTime shippedAt;
    @SheetColumn(name = "Delivered_At", order = 8) private LocalDateTime deliveredAt;
    @SheetColumn(name = "Created_At", order = 9) private LocalDateTime createdAt;
    @SheetColumn(name = "Updated_At", order = 10) private LocalDateTime updatedAt;
}
