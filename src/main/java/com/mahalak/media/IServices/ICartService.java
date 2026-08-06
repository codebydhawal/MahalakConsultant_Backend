package com.mahalak.media.IServices;

import com.mahalak.media.dto.request.CartRequest;
import com.mahalak.media.dto.response.CartResponse;
import com.mahalak.media.dto.response.CartSummaryResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface ICartService {

    CartSummaryResponse addToCart(@Valid CartRequest request);

    CartSummaryResponse getAllCartItems();

    CartSummaryResponse updateCart(String cartId, Integer quantity);

    CartSummaryResponse deleteCartItem(String cartId);

    CartSummaryResponse clearCart();
}
