package com.mahalak.media.servicesImpl;

import com.mahalak.media.IServices.ICartService;
import com.mahalak.media.auth.SecurityUtil;
import com.mahalak.media.dto.request.CartRequest;
import com.mahalak.media.dto.response.CartResponse;
import com.mahalak.media.dto.response.CartSummaryResponse;
import com.mahalak.media.entity.Cart;
import com.mahalak.media.entity.Product;
import com.mahalak.media.exception.BadRequestException;
import com.mahalak.media.exception.ResourceNotFoundException;
import com.mahalak.media.framework.GoogleEntityManager;
import com.mahalak.media.mapper.CartMapper;
import com.mahalak.media.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final GoogleEntityManager entityManager;

    private final CartMapper cartMapper;

    private final ProductMapper productMapper;

    @Override
    public CartSummaryResponse addToCart(CartRequest request) {

        Long userId = SecurityUtil.getCurrentUserId();

        Product product = entityManager.findById(Product.class, request.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found."));

        if (product.getStock() < request.getQuantity()) {
            throw new BadRequestException("Insufficient stock available.");
        }

        Optional<Cart> existingCart = entityManager.findAll(Cart.class)
                .stream()
                .filter(cart ->
                        cart.getUserId().equals(userId.toString()) &&
                                cart.getProductId().equals(request.getProductId()))
                .findFirst();

        Cart cart;

        if (existingCart.isPresent()) {

            cart = existingCart.get();

            int newQuantity = cart.getQuantity() + request.getQuantity();

            if (newQuantity > product.getStock()) {
                throw new BadRequestException("Requested quantity exceeds available stock.");
            }

            cart.setQuantity(newQuantity);
            cart.setUpdatedAt(LocalDateTime.now());

            entityManager.update(cart);

        } else {

            cart = cartMapper.toEntity(request);

            cart.setUserId(userId.toString());
            cart.setCreatedAt(LocalDateTime.now());
            cart.setUpdatedAt(LocalDateTime.now());

            entityManager.save(cart);
        }

        return buildCartSummary(userId);
    }

    @Override
    public CartSummaryResponse getAllCartItems() {

        Long userId = SecurityUtil.getCurrentUserId();

        List<Cart> carts = entityManager.findAll(Cart.class)
                .stream()
                .filter(cart ->
                        cart.getUserId().equals(userId.toString()))
                .toList();

        List<CartResponse> cartResponses = new ArrayList<>();

        double cartTotal = 0.0;
        int totalItems = 0;

        for (Cart cart : carts) {

            Product product = entityManager.findById(Product.class, cart.getProductId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Product not found: " + cart.getProductId()));

            double subTotal = product.getPrice() * cart.getQuantity();

            CartResponse response = CartResponse.builder()
                    .cartId(cart.getCartId())
                    .quantity(cart.getQuantity())
                    .subTotal(subTotal)
                    .product(productMapper.toResponse(product))
                    .build();

            cartResponses.add(response);

            cartTotal += subTotal;
            totalItems += cart.getQuantity();
        }

        return CartSummaryResponse.builder()
                .items(cartResponses)
                .totalProducts(cartResponses.size())
                .totalItems(totalItems)
                .cartTotal(cartTotal)
                .build();
    }

    @Override
    public CartSummaryResponse updateCart(String cartId, Integer quantity) {

        Long userId = SecurityUtil.getCurrentUserId();
        Cart cart = entityManager.findById(Cart.class, cartId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart item not found."));

        // Security check
        if (!cart.getUserId().equals(userId.toString())) {
            throw new BadRequestException("You cannot update another user's cart.");
        }

        Product product = entityManager.findById(Product.class, cart.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found."));

        if (quantity > product.getStock()) {
            throw new BadRequestException("Requested quantity exceeds available stock.");
        }

        cart.setQuantity(quantity);
        cart.setUpdatedAt(LocalDateTime.now());

        entityManager.update(cart);

        return buildCartSummary(userId);
    }


    @Override
    public CartSummaryResponse deleteCartItem(String cartId) {

        Long userId = SecurityUtil.getCurrentUserId();

        Cart cart = entityManager.findById(Cart.class, cartId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart item not found."));

        if (!cart.getUserId().equals(userId.toString())) {
            throw new BadRequestException("You cannot delete another user's cart item.");
        }

        entityManager.delete(Cart.class, cartId);

        return buildCartSummary(userId);
    }

    @Override
    public CartSummaryResponse clearCart() {

        Long userId = SecurityUtil.getCurrentUserId();

        List<Cart> carts = entityManager.findAll(Cart.class)
                .stream()
                .filter(cart ->
                        cart.getUserId().equals(userId.toString()))
                .toList();

        for (Cart cart : carts) {
            entityManager.delete(Cart.class, cart.getCartId());
        }

        return buildCartSummary(userId);
    }

    private CartSummaryResponse buildCartSummary(Long userId) {

        List<Cart> carts = entityManager.findAll(Cart.class)
                .stream()
                .filter(cart -> cart.getUserId().equals(userId.toString()))
                .toList();

        // Read ALL products once
        List<Product> products = entityManager.findAll(Product.class);

        // Create product lookup map
        Map<String, Product> productMap =
                products.stream()
                        .collect(Collectors.toMap(
                                Product::getProductId,
                                Function.identity()
                        ));

        List<CartResponse> items = new ArrayList<>();

        double cartTotal = 0;
        int totalItems = 0;

        for (Cart cart : carts) {

            Product product = productMap.get(cart.getProductId());

            if (product == null) {
                throw new ResourceNotFoundException(
                        "Product not found: " + cart.getProductId());
            }

            double subTotal = product.getPrice() * cart.getQuantity();

            CartResponse response = CartResponse.builder()
                    .cartId(cart.getCartId())
                    .quantity(cart.getQuantity())
                    .subTotal(subTotal)
                    .product(productMapper.toResponse(product))
                    .build();

            items.add(response);

            cartTotal += subTotal;
            totalItems += cart.getQuantity();
        }

        return CartSummaryResponse.builder()
                .items(items)
                .totalProducts(items.size())
                .totalItems(totalItems)
                .cartTotal(cartTotal)
                .build();
    }
}
