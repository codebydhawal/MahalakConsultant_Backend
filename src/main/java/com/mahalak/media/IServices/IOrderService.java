package com.mahalak.media.IServices;

import com.mahalak.media.dto.request.OrderRequest;
import com.mahalak.media.dto.response.OrderResponse;
import com.mahalak.media.dto.response.CartSummaryResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface IOrderService {
    OrderResponse createOrder(@Valid OrderRequest request);

    List<OrderResponse> getMyOrders();

    OrderResponse getOrderById(String orderId);

    OrderResponse cancelOrder(String orderId);

    CartSummaryResponse reorder(String orderId);

    OrderResponse trackOrder(String orderId);

    List<OrderResponse> getAllOrdersForAdmin();

    OrderResponse getOrderForAdmin(String orderId);

    OrderResponse updateOrderStatus(String orderId, String status);
}
