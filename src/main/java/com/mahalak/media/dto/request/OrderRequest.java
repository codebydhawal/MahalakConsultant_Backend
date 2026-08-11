package com.mahalak.media.dto.request;

import com.mahalak.media.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    private String addressId;

    private PaymentMethod paymentMethod;

    private String couponCode;
}
