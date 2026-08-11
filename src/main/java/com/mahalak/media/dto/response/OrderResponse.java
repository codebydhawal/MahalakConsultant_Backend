package com.mahalak.media.dto.response;

import com.mahalak.media.enums.OrderStatus;
import com.mahalak.media.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private String orderId;

    /** ID of the payment created with this order; needed for online proof submission. */
    private String paymentId;

    private String orderNumber;

    private String userId;

    private String addressId;

    private List<OrderItemResponse> items;

    private Double productTotal;

    private Double taxAmount;

    private Double discountAmount;

    private Double shippingAmount;

    private Double finalAmount;

    private PaymentMethod paymentMethod;

    private OrderStatus orderStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
