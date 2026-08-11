package com.mahalak.media.entity;

import com.mahalak.media.annotations.Sheet;
import com.mahalak.media.annotations.SheetColumn;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Sheet(name = "Inventory_Movement")
public class InventoryMovement {
    @SheetColumn(name = "Movement_Id", id = true, prefix = "INV", order = 1) private String movementId;
    @SheetColumn(name = "Product_Id", order = 2) private String productId;
    @SheetColumn(name = "Order_Id", order = 3) private String orderId;
    @SheetColumn(name = "Quantity_Change", order = 4) private Integer quantityChange;
    @SheetColumn(name = "Stock_After", order = 5) private Integer stockAfter;
    @SheetColumn(name = "Reason", order = 6) private String reason;
    @SheetColumn(name = "Created_At", order = 7) private LocalDateTime createdAt;
}
