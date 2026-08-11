package com.mahalak.media.entity;

import com.mahalak.media.annotations.Sheet;
import com.mahalak.media.annotations.SheetColumn;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Sheet(name = "Coupon")
public class Coupon {
    @SheetColumn(name = "Coupon_Id", id = true, prefix = "CPN", order = 1) private String couponId;
    @SheetColumn(name = "Code", order = 2) private String code;
    @SheetColumn(name = "Discount_Percent", order = 3) private Double discountPercent;
    @SheetColumn(name = "Min_Order_Amount", order = 4) private Double minOrderAmount;
    @SheetColumn(name = "Max_Discount_Amount", order = 5) private Double maxDiscountAmount;
    @SheetColumn(name = "Max_Uses_Per_User", order = 6) private Integer maxUsesPerUser;
    @SheetColumn(name = "Active", order = 7) private Boolean active;
    @SheetColumn(name = "Start_Date", order = 8) private LocalDateTime startDate;
    @SheetColumn(name = "End_Date", order = 9) private LocalDateTime endDate;
    @SheetColumn(name = "Created_At", order = 10) private LocalDateTime createdAt;
    @SheetColumn(name = "Updated_At", order = 11) private LocalDateTime updatedAt;
}
