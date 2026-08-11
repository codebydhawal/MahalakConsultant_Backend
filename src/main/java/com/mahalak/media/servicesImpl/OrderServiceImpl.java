package com.mahalak.media.servicesImpl;

import com.mahalak.media.IServices.IOrderService;
import com.mahalak.media.IServices.IPaymentService;
import com.mahalak.media.auth.SecurityUtil;
import com.mahalak.media.dto.request.OrderRequest;
import com.mahalak.media.dto.response.CartResponse;
import com.mahalak.media.dto.response.CartSummaryResponse;
import com.mahalak.media.dto.response.OrderItemResponse;
import com.mahalak.media.dto.response.OrderResponse;
import com.mahalak.media.dto.response.PaymentResponse;
import com.mahalak.media.entity.Address;
import com.mahalak.media.entity.Cart;
import com.mahalak.media.entity.DiscountRule;
import com.mahalak.media.entity.Order;
import com.mahalak.media.entity.OrderItem;
import com.mahalak.media.entity.Payment;
import com.mahalak.media.entity.Product;
import com.mahalak.media.entity.ShippingRule;
import com.mahalak.media.entity.TaxRule;
import com.mahalak.media.enums.OrderStatus;
import com.mahalak.media.enums.PaymentMethod;
import com.mahalak.media.enums.PaymentStatus;
import com.mahalak.media.exception.BadRequestException;
import com.mahalak.media.exception.ResourceNotFoundException;
import com.mahalak.media.framework.GoogleEntityManager;
import com.mahalak.media.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private static final DateTimeFormatter ORDER_NUMBER_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final GoogleEntityManager entityManager;
    private final IPaymentService paymentService;
    private final ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(OrderRequest request) {
        validateOrderRequest(request);
        Long currentUserId = SecurityUtil.getCurrentUserId();
        String userId = currentUserId.toString();
        validateAddress(request.getAddressId(), userId);

        List<Cart> carts = entityManager.findAll(Cart.class).stream()
                .filter(cart -> userId.equals(cart.getUserId()))
                .toList();
        if (carts.isEmpty()) {
            throw new BadRequestException("Cart is empty. Cannot create order.");
        }

        List<CheckoutLine> lines = buildCheckoutLines(carts);
        Totals totals = calculateTotals(lines);
        LocalDateTime now = LocalDateTime.now();

        Order order = Order.builder()
                .orderNumber("ORD-" + ORDER_NUMBER_TIME.format(now) + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .userId(userId)
                .addressId(request.getAddressId())
                .productTotal(totals.productTotal())
                .taxAmount(totals.taxAmount())
                .discountAmount(totals.discountAmount())
                .shippingAmount(totals.shippingAmount())
                .finalAmount(totals.finalAmount())
                .paymentMethod(request.getPaymentMethod().name())
                .orderStatus(request.getPaymentMethod() == PaymentMethod.COD
                        ? OrderStatus.CONFIRMED.name() : OrderStatus.PAYMENT_PENDING.name())
                .createdAt(now)
                .updatedAt(now)
                .build();

        List<OrderItem> savedItems = new ArrayList<>();
        List<Cart> deletedCarts = new ArrayList<>();
        List<CheckoutLine> stockAdjustedLines = new ArrayList<>();
        boolean orderSaved = false;
        boolean paymentSaved = false;
        try {
            entityManager.save(order);
            orderSaved = true;

            for (CheckoutLine line : lines) {
                OrderItem item = OrderItem.builder()
                        .orderId(order.getOrderId())
                        .productId(line.product().getProductId())
                        .productName(line.product().getName())
                        .productPrice(line.unitPrice())
                        .quantity(line.quantity())
                        .subtotal(line.subtotal())
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
                entityManager.save(item);
                savedItems.add(item);
            }

            for (CheckoutLine line : lines) {
                Product product = line.product();
                product.setStock(product.getStock() - line.quantity());
                product.setUpdatedAt(now);
                entityManager.update(product);
                stockAdjustedLines.add(line);
            }

            PaymentResponse payment = paymentService.createInitialPayment(
                    order.getOrderId(), request.getPaymentMethod(), totals.finalAmount());
            paymentSaved = true;

            for (Cart cart : carts) {
                entityManager.delete(Cart.class, cart.getCartId());
                deletedCarts.add(cart);
            }

            OrderResponse response = toResponse(order, savedItems);
            response.setPaymentId(payment.getPaymentId());
            return response;
        } catch (RuntimeException exception) {
            compensateFailedCheckout(order, orderSaved, paymentSaved, savedItems, stockAdjustedLines, deletedCarts);
            throw exception;
        }
    }

    @Override
    public List<OrderResponse> getMyOrders() {
        String userId = SecurityUtil.getCurrentUserId().toString();
        return entityManager.findAll(Order.class).stream()
                .filter(order -> userId.equals(order.getUserId()))
                .sorted(Comparator.comparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrderById(String orderId) {
        Order order = findOrder(orderId);
        assertOrderOwner(order);
        return toResponse(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse cancelOrder(String orderId) {
        Order order = findOrder(orderId);
        assertOrderOwner(order);

        Payment payment = findPaymentByOrderId(orderId);
        if (!canCustomerCancel(order, payment)) {
            throw new BadRequestException("This order can no longer be cancelled.");
        }

        List<OrderItem> items = findOrderItems(orderId);
        LocalDateTime now = LocalDateTime.now();
        for (OrderItem item : items) {
            Product product = findProduct(item.getProductId());
            product.setStock(product.getStock() + item.getQuantity());
            product.setUpdatedAt(now);
            entityManager.update(product);
        }

        order.setOrderStatus(OrderStatus.CANCELLED.name());
        order.setUpdatedAt(now);
        payment.setPaymentStatus(PaymentStatus.CANCELLED.name());
        payment.setUpdatedAt(now);
        entityManager.update(order);
        entityManager.update(payment);
        return toResponse(order, items);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartSummaryResponse reorder(String orderId) {
        Order order = findOrder(orderId);
        assertOrderOwner(order);
        String userId = SecurityUtil.getCurrentUserId().toString();
        List<OrderItem> items = findOrderItems(orderId);
        if (items.isEmpty()) {
            throw new BadRequestException("This order has no products to reorder.");
        }

        List<Cart> existingCarts = entityManager.findAll(Cart.class).stream()
                .filter(cart -> userId.equals(cart.getUserId()))
                .toList();
        Map<String, Cart> cartsByProduct = existingCarts.stream()
                .collect(Collectors.toMap(Cart::getProductId, cart -> cart, (first, ignored) -> first));

        for (OrderItem item : items) {
            Product product = findProduct(item.getProductId());
            validatePurchasableProduct(product);
            int existingQuantity = cartsByProduct.containsKey(item.getProductId())
                    ? cartsByProduct.get(item.getProductId()).getQuantity() : 0;
            if (existingQuantity + item.getQuantity() > product.getStock()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }
        }

        LocalDateTime now = LocalDateTime.now();
        for (OrderItem item : items) {
            Cart existing = cartsByProduct.get(item.getProductId());
            if (existing == null) {
                Cart cart = Cart.builder()
                        .userId(userId)
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
                entityManager.save(cart);
            } else {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                existing.setUpdatedAt(now);
                entityManager.update(existing);
            }
        }

        return buildCartSummary(userId);
    }

    @Override
    public OrderResponse trackOrder(String orderId) {
        return getOrderById(orderId);
    }

    @Override
    public List<OrderResponse> getAllOrdersForAdmin() {
        return entityManager.findAll(Order.class).stream()
                .sorted(Comparator.comparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrderForAdmin(String orderId) {
        return toResponse(findOrder(orderId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse updateOrderStatus(String orderId, String status) {
        Order order = findOrder(orderId);
        OrderStatus targetStatus;
        try {
            targetStatus = OrderStatus.valueOf(status.trim().toUpperCase());
        } catch (Exception exception) {
            throw new BadRequestException("Invalid order status: " + status);
        }

        OrderStatus currentStatus = parseOrderStatus(order);
        if (!isAllowedAdminTransition(currentStatus, targetStatus)) {
            throw new BadRequestException("Cannot change order status from " + currentStatus + " to " + targetStatus + ".");
        }

        order.setOrderStatus(targetStatus.name());
        order.setUpdatedAt(LocalDateTime.now());
        entityManager.update(order);
        return toResponse(order);
    }

    private void validateOrderRequest(OrderRequest request) {
        if (request == null) {
            throw new BadRequestException("Order request is required.");
        }
        if (request.getAddressId() == null || request.getAddressId().isBlank()) {
            throw new BadRequestException("Address ID is required.");
        }
        if (request.getPaymentMethod() == null) {
            throw new BadRequestException("Payment method is required.");
        }
    }

    private void validateAddress(String addressId, String userId) {
        Address address = entityManager.findById(Address.class, addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found."));
        if (!userId.equals(address.getUserId()) || Boolean.TRUE.equals(address.getIsAddressDeleted())) {
            throw new BadRequestException("You cannot use this address for the order.");
        }
    }

    private List<CheckoutLine> buildCheckoutLines(List<Cart> carts) {
        Map<String, Integer> requestedQuantities = new HashMap<>();
        for (Cart cart : carts) {
            if (cart.getProductId() == null || cart.getQuantity() == null || cart.getQuantity() <= 0) {
                throw new BadRequestException("Cart contains an invalid item.");
            }
            requestedQuantities.merge(cart.getProductId(), cart.getQuantity(), Integer::sum);
        }

        List<CheckoutLine> lines = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : requestedQuantities.entrySet()) {
            Product product = findProduct(entry.getKey());
            validatePurchasableProduct(product);
            if (product.getStock() < entry.getValue()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }
            double unitPrice = requireNonNegative(product.getPrice(), "Product price is invalid for: " + product.getName());
            lines.add(new CheckoutLine(product, entry.getValue(), unitPrice, round(unitPrice * entry.getValue())));
        }
        return lines;
    }

    private Totals calculateTotals(List<CheckoutLine> lines) {
        double productTotal = round(lines.stream().mapToDouble(CheckoutLine::subtotal).sum());
        LocalDateTime now = LocalDateTime.now();
        double taxRate = activeTaxRate(now);
        double discountRate = activeDiscountRate(now);
        double shippingRate = activeShippingRate(now);
        double taxAmount = round(productTotal * taxRate / 100);
        double discountAmount = round(productTotal * discountRate / 100);
        double shippingAmount = round(productTotal * shippingRate / 100);
        return new Totals(productTotal, taxAmount, discountAmount, shippingAmount,
                round(productTotal + taxAmount + shippingAmount - discountAmount));
    }

    private double activeTaxRate(LocalDateTime now) {
        return entityManager.findAll(TaxRule.class).stream()
                .filter(rule -> isRuleActive(rule.getActive(), rule.getStartDate(), rule.getEndDate(), now))
                .mapToDouble(rule -> requireNonNegative(rule.getRate(), "Tax rule rate is invalid."))
                .sum();
    }

    private double activeDiscountRate(LocalDateTime now) {
        return entityManager.findAll(DiscountRule.class).stream()
                .filter(rule -> isRuleActive(rule.getActive(), rule.getStartDate(), rule.getEndDate(), now))
                .mapToDouble(rule -> requireNonNegative(rule.getRate(), "Discount rule rate is invalid."))
                .sum();
    }

    private double activeShippingRate(LocalDateTime now) {
        return entityManager.findAll(ShippingRule.class).stream()
                .filter(rule -> isRuleActive(rule.getActive(), rule.getStartDate(), rule.getEndDate(), now))
                .mapToDouble(rule -> requireNonNegative(rule.getRate(), "Shipping rule rate is invalid."))
                .sum();
    }

    private boolean isRuleActive(Boolean active, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime now) {
        return Boolean.TRUE.equals(active)
                && (startDate == null || !now.isBefore(startDate))
                && (endDate == null || !now.isAfter(endDate));
    }

    private void compensateFailedCheckout(Order order, boolean orderSaved, boolean paymentSaved,
                                           List<OrderItem> savedItems, List<CheckoutLine> stockAdjustedLines,
                                           List<Cart> deletedCarts) {
        // Google Sheets has no rollback support, so reverse successful side effects best-effort.
        for (Cart cart : deletedCarts) {
            try { entityManager.save(cart); } catch (RuntimeException ignored) { }
        }
        if (paymentSaved) {
            try {
                entityManager.findAll(Payment.class).stream()
                        .filter(payment -> order.getOrderId().equals(payment.getOrderId()))
                        .findFirst()
                        .ifPresent(payment -> entityManager.delete(Payment.class, payment.getPaymentId()));
            } catch (RuntimeException ignored) { }
        }
        for (CheckoutLine line : stockAdjustedLines) {
            try {
                Product product = findProduct(line.product().getProductId());
                product.setStock(product.getStock() + line.quantity());
                product.setUpdatedAt(LocalDateTime.now());
                entityManager.update(product);
            } catch (RuntimeException ignored) { }
        }
        for (OrderItem item : savedItems) {
            try { entityManager.delete(OrderItem.class, item.getOrderItemId()); } catch (RuntimeException ignored) { }
        }
        if (orderSaved) {
            try { entityManager.delete(Order.class, order.getOrderId()); } catch (RuntimeException ignored) { }
        }
    }

    private boolean canCustomerCancel(Order order, Payment payment) {
        OrderStatus status = parseOrderStatus(order);
        if (PaymentStatus.VERIFIED.name().equals(payment.getPaymentStatus())) {
            return false;
        }
        return status == OrderStatus.PAYMENT_PENDING
                || status == OrderStatus.PAYMENT_VERIFICATION_PENDING
                || status == OrderStatus.PAYMENT_FAILED
                || (status == OrderStatus.CONFIRMED && PaymentMethod.COD.name().equals(order.getPaymentMethod()));
    }

    private boolean isAllowedAdminTransition(OrderStatus from, OrderStatus to) {
        return (from == OrderStatus.CONFIRMED && (to == OrderStatus.PROCESSING || to == OrderStatus.CANCELLED))
                || (from == OrderStatus.PROCESSING && (to == OrderStatus.SHIPPED || to == OrderStatus.CANCELLED))
                || (from == OrderStatus.SHIPPED && to == OrderStatus.DELIVERED);
    }

    private CartSummaryResponse buildCartSummary(String userId) {
        List<CartResponse> responses = new ArrayList<>();
        double cartTotal = 0;
        int totalItems = 0;
        for (Cart cart : entityManager.findAll(Cart.class)) {
            if (!userId.equals(cart.getUserId())) continue;
            Product product = findProduct(cart.getProductId());
            double subtotal = round(product.getPrice() * cart.getQuantity());
            responses.add(CartResponse.builder()
                    .cartId(cart.getCartId())
                    .product(productMapper.toResponse(product))
                    .quantity(cart.getQuantity())
                    .subTotal(subtotal)
                    .build());
            cartTotal += subtotal;
            totalItems += cart.getQuantity();
        }
        return CartSummaryResponse.builder().items(responses).totalProducts(responses.size())
                .totalItems(totalItems).cartTotal(round(cartTotal)).build();
    }

    private Order findOrder(String orderId) {
        return entityManager.findById(Order.class, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found."));
    }

    private Product findProduct(String productId) {
        return entityManager.findById(Product.class, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
    }

    private Payment findPaymentByOrderId(String orderId) {
        return entityManager.findAll(Payment.class).stream()
                .filter(payment -> orderId.equals(payment.getOrderId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for this order."));
    }

    private List<OrderItem> findOrderItems(String orderId) {
        return entityManager.findAll(OrderItem.class).stream()
                .filter(item -> orderId.equals(item.getOrderId()))
                .toList();
    }

    private void assertOrderOwner(Order order) {
        if (!SecurityUtil.getCurrentUserId().toString().equals(order.getUserId())) {
            throw new BadRequestException("You cannot access another user's order.");
        }
    }

    private void validatePurchasableProduct(Product product) {
        if (Boolean.TRUE.equals(product.getIsProductDeleted()) || !"ACTIVE".equalsIgnoreCase(product.getStatus())) {
            throw new BadRequestException("Product is not available: " + product.getName());
        }
        if (product.getStock() == null || product.getStock() < 0) {
            throw new BadRequestException("Product stock is invalid: " + product.getName());
        }
    }

    private OrderStatus parseOrderStatus(Order order) {
        try { return OrderStatus.valueOf(order.getOrderStatus()); }
        catch (Exception exception) { throw new BadRequestException("Order has an invalid status."); }
    }

    private double requireNonNegative(Double value, String message) {
        if (value == null || value < 0) throw new BadRequestException(message);
        return value;
    }

    private double round(double amount) {
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private OrderResponse toResponse(Order order) {
        return toResponse(order, findOrderItems(order.getOrderId()));
    }

    private OrderResponse toResponse(Order order, List<OrderItem> items) {
        List<OrderItemResponse> responses = items.stream().map(item -> {
            OrderItemResponse response = new OrderItemResponse();
            response.setOrderItemId(item.getOrderItemId());
            response.setOrderId(item.getOrderId());
            response.setProductId(item.getProductId());
            response.setProductName(item.getProductName());
            response.setProductPrice(item.getProductPrice());
            response.setQuantity(item.getQuantity());
            return response;
        }).toList();
        OrderResponse response = new OrderResponse(order.getOrderId(), null, order.getOrderNumber(), order.getUserId(), order.getAddressId(), responses,
                order.getProductTotal(), order.getTaxAmount(), order.getDiscountAmount(), order.getShippingAmount(), order.getFinalAmount(),
                PaymentMethod.valueOf(order.getPaymentMethod()), OrderStatus.valueOf(order.getOrderStatus()), order.getCreatedAt(), order.getUpdatedAt());
        entityManager.findAll(Payment.class).stream()
                .filter(payment -> order.getOrderId().equals(payment.getOrderId()))
                .findFirst()
                .ifPresent(payment -> response.setPaymentId(payment.getPaymentId()));
        return response;
    }

    private record CheckoutLine(Product product, int quantity, double unitPrice, double subtotal) { }
    private record Totals(double productTotal, double taxAmount, double discountAmount, double shippingAmount, double finalAmount) { }
}
