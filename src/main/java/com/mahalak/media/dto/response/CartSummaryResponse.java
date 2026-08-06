package com.mahalak.media.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartSummaryResponse {

    private List<CartResponse> items;

    private Integer totalProducts;

    private Integer totalItems;

    private Double cartTotal;
}
