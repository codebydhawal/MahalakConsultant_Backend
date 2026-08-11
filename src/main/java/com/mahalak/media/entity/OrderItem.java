package com.mahalak.media.entity;

import com.mahalak.media.annotations.Sheet;
import com.mahalak.media.annotations.SheetColumn;
import com.mahalak.media.framework.CacheableEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Sheet(name = "Order_Item")
@CacheableEntity(ttl = 300)
public class OrderItem {

    @SheetColumn(name = "Order_Item_Id", id = true, prefix = "ORI", order = 1)
    private String orderItemId;

    @SheetColumn(name = "Order_Id", order = 2)
    private String orderId;

    @SheetColumn(name = "Product_Id", order = 3)
    private String productId;

    @SheetColumn(name = "Product_Name", order = 4)
    private String productName;

    @SheetColumn(name = "Product_Price", order = 5)
    private Double productPrice;

    @SheetColumn(name = "Quantity", order = 6)
    private Integer quantity;

    @SheetColumn(name = "Subtotal", order = 7)
    private Double subtotal;

    @SheetColumn(name = "Created_At", order = 8)
    private LocalDateTime createdAt;

    @SheetColumn(name = "Updated_At", order = 9)
    private LocalDateTime updatedAt;
}