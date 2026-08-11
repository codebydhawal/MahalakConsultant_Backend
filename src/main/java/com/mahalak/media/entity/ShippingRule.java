package com.mahalak.media.entity;

import com.mahalak.media.annotations.Sheet;
import com.mahalak.media.annotations.SheetColumn;
import com.mahalak.media.framework.CacheableEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Sheet(name = "ShippingRule")
@CacheableEntity(ttl = 600)
public class ShippingRule {

    @SheetColumn(name = "Shipping_Id", id = true, prefix = "SR", order = 1)
    private String shippingId;

    @SheetColumn(name = "Name", order = 2)
    private String name;

    @SheetColumn(name = "Rate", order = 3)
    private Double rate;

    @SheetColumn(name = "Active", order = 4)
    private Boolean active;

    @SheetColumn(name = "Start_Date", order = 5)
    private LocalDateTime startDate;

    @SheetColumn(name = "End_Date", order = 6)
    private LocalDateTime endDate;

    @SheetColumn(name = "Created_At", order = 7)
    private LocalDateTime createdAt;

    @SheetColumn(name = "Updated_At", order = 8)
    private LocalDateTime updatedAt;
}
