package com.mahalak.media.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private String orderItemId;

    private String orderId;

    private String productId;

    private String productName;

    private Double productPrice;

    private Integer quantity;
}
