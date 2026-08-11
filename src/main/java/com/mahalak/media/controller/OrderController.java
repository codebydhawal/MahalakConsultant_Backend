package com.mahalak.media.controller;

import com.mahalak.media.IServices.IOrderService;
import com.mahalak.media.dto.request.OrderRequest;
import com.mahalak.media.dto.response.OrderResponse;
import com.mahalak.media.dto.wrapper.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/order")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;


    // =========================================================
    // CREATE ORDER
    // =========================================================

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest request) {

        OrderResponse response =
                orderService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "Order created successfully.",
                        response));
    }


    // =========================================================
    // GET MY ORDERS
    // =========================================================

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<?>> getMyOrders() {

        Object response =
                orderService.getMyOrders();

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Orders fetched successfully.",
                        response));
    }


    // =========================================================
    // GET ORDER BY ID
    // =========================================================

    @GetMapping("/get")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @RequestParam String orderId) {

        OrderResponse response =
                orderService.getOrderById(orderId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Order fetched successfully.",
                        response));
    }


    // =========================================================
    // CANCEL ORDER
    // =========================================================

    @PatchMapping("/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @RequestParam String orderId) {

        OrderResponse response =
                orderService.cancelOrder(orderId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Order cancelled successfully.",
                        response));
    }


    // =========================================================
    // REORDER
    // =========================================================

    @PostMapping("/reorder")
    public ResponseEntity<ApiResponse<?>> reorder(
            @RequestParam String orderId) {

        Object response =
                orderService.reorder(orderId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Products added to cart successfully.",
                        response));
    }


    // =========================================================
    // TRACK ORDER
    // =========================================================

    @GetMapping("/track")
    public ResponseEntity<ApiResponse<?>> trackOrder(
            @RequestParam String orderId) {

        Object response =
                orderService.trackOrder(orderId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Order tracking details fetched successfully.",
                        response));
    }


    // =========================================================
    // ADMIN - GET ALL ORDERS
    // =========================================================

    @GetMapping("/admin/get-all")
    public ResponseEntity<ApiResponse<?>> getAllOrdersForAdmin() {

        Object response =
                orderService.getAllOrdersForAdmin();

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "All orders fetched successfully.",
                        response));
    }


    // =========================================================
    // ADMIN - GET ORDER
    // =========================================================

    @GetMapping("/admin/get")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderForAdmin(
            @RequestParam String orderId) {

        OrderResponse response =
                orderService.getOrderForAdmin(orderId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Order details fetched successfully.",
                        response));
    }


    // =========================================================
    // ADMIN - UPDATE ORDER STATUS
    // =========================================================

    @PatchMapping("/admin/update-status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @RequestParam String orderId,
            @RequestParam String status) {

        OrderResponse response =
                orderService.updateOrderStatus(
                        orderId,
                        status);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Order status updated successfully.",
                        response));
    }
}
