package com.mahalak.media.entity;

import com.mahalak.media.annotations.Sheet;
import com.mahalak.media.annotations.SheetColumn;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Sheet(name = "Order_Status_History")
public class OrderStatusHistory {
    @SheetColumn(name = "History_Id", id = true, prefix = "OSH", order = 1) private String historyId;
    @SheetColumn(name = "Order_Id", order = 2) private String orderId;
    @SheetColumn(name = "From_Status", order = 3) private String fromStatus;
    @SheetColumn(name = "To_Status", order = 4) private String toStatus;
    @SheetColumn(name = "Changed_By", order = 5) private String changedBy;
    @SheetColumn(name = "Note", order = 6) private String note;
    @SheetColumn(name = "Changed_At", order = 7) private LocalDateTime changedAt;
}
