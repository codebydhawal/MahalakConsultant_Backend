package com.mahalak.media.IServices;

import com.mahalak.media.dto.request.ProductRequest;
import com.mahalak.media.dto.response.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductService {

    /**
     * Add Product
     */
    ProductResponse addProduct(ProductRequest request, MultipartFile file);

    /**
     * Get Product By Id
     */
    ProductResponse getProductById(String productId);

    /**
     * Get All Products
     */
    List<ProductResponse> getAllProducts();

    /**
     * Update Product
     */
    ProductResponse updateProduct(String productId, ProductRequest request, MultipartFile file);

    /**
     * Delete Product
     */
    ProductResponse deleteProduct(String productId);
}