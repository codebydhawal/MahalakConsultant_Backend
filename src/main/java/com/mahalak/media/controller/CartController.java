package com.mahalak.media.controller;

import com.mahalak.media.IServices.ICartService;
import com.mahalak.media.dto.request.CartRequest;
import com.mahalak.media.dto.response.CartSummaryResponse;
import com.mahalak.media.dto.wrapper.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/cart")
@RequiredArgsConstructor
public class CartController {

    private final ICartService cartService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> addToCart(
            @Valid @RequestBody CartRequest request) {

        CartSummaryResponse response = cartService.addToCart(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "Product added to cart successfully.",
                        response));
    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> getAllCartItems() {

        CartSummaryResponse response = cartService.getAllCartItems();

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Cart fetched successfully.",
                        response));
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> updateCart(
            @RequestParam String cartId,
            @RequestParam Integer quantity) {

        CartSummaryResponse response =
                cartService.updateCart(cartId, quantity);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Cart updated successfully.",
                        response));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> deleteCartItem(
            @RequestParam String cartId) {

        CartSummaryResponse response =
                cartService.deleteCartItem(cartId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Cart item deleted successfully.",
                        response));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> clearCart() {

        CartSummaryResponse response =
                cartService.clearCart();

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Cart cleared successfully.",
                        response));
    }
}