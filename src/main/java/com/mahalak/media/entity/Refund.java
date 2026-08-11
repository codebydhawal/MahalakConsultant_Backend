package com.mahalak.media.entity;

import com.mahalak.media.annotations.Sheet;
import com.mahalak.media.annotations.SheetColumn;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Sheet(name = "Refund")
public class Refund {
    @SheetColumn(name = "Refund_Id", id = true, prefix = "RFD", order = 1) private String refundId;
    @SheetColumn(name = "Order_Id", order = 2) private String orderId;
    @SheetColumn(name = "Payment_Id", order = 3) private String paymentId;
    @SheetColumn(name = "Amount", order = 4) private Double amount;
    @SheetColumn(name = "Status", order = 5) private String status;
    @SheetColumn(name = "Reason", order = 6) private String reason;
    @SheetColumn(name = "Processed_By", order = 7) private String processedBy;
    @SheetColumn(name = "Processed_At", order = 8) private LocalDateTime processedAt;
    @SheetColumn(name = "Created_At", order = 9) private LocalDateTime createdAt;
    @SheetColumn(name = "Updated_At", order = 10) private LocalDateTime updatedAt;
}
