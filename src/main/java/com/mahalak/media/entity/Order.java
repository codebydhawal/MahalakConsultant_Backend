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
@Sheet(name = "Order")
@CacheableEntity(ttl = 300)
public class Order {

    @SheetColumn(name = "Order_Id", id = true, prefix = "ORD", order = 1)
    private String orderId;

    @SheetColumn(name = "Order_Number", order = 2)
    private String orderNumber;

    @SheetColumn(name = "User_Id", order = 3)
    private String userId;

    @SheetColumn(name = "Address_Id", order = 4)
    private String addressId;

    @SheetColumn(name = "Product_Total", order = 5)
    private Double productTotal;

    @SheetColumn(name = "Tax_Amount", order = 6)
    private Double taxAmount;

    @SheetColumn(name = "Discount_Amount", order = 7)
    private Double discountAmount;

    @SheetColumn(name = "Shipping_Amount", order = 8)
    private Double shippingAmount;

    @SheetColumn(name = "Final_Amount", order = 9)
    private Double finalAmount;

    @SheetColumn(name = "Coupon_Code", order = 10)
    private String couponCode;

    @SheetColumn(name = "Coupon_Discount_Amount", order = 11)
    private Double couponDiscountAmount;

    @SheetColumn(name = "Payment_Method", order = 12)
    private String paymentMethod;

    @SheetColumn(name = "Order_Status", order = 13)
    private String orderStatus;

    @SheetColumn(name = "Created_At", order = 14)
    private LocalDateTime createdAt;

    @SheetColumn(name = "Updated_At", order = 15)
    private LocalDateTime updatedAt;
}
