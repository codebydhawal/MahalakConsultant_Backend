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
@Sheet(name = "Cart")
@CacheableEntity(ttl = 300)
public class Cart {

    @SheetColumn(name = "Cart_Id", id = true, prefix = "CRT", order = 1)
    private String cartId;

    @SheetColumn(name = "User_Id", order = 2)
    private String userId;

    @SheetColumn(name = "Product_Id", order = 3)
    private String productId;

    @SheetColumn(name = "Quantity", order = 4)
    private Integer quantity;

    @SheetColumn(name = "Created_At", order = 5)
    private LocalDateTime createdAt;

    @SheetColumn(name = "Updated_At", order = 6)
    private LocalDateTime updatedAt;
}
