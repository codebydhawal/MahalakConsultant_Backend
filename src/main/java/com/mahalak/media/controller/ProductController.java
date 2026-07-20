package com.mahalak.media.controller;

import com.mahalak.media.IServices.IProductService;
import com.mahalak.media.dto.request.ProductRequest;
import com.mahalak.media.dto.response.ProductResponse;
import com.mahalak.media.dto.wrapper.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/rest/product")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    /**
     * Add Product
     */
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponse>> addProduct(
            @Valid @RequestPart("request") ProductRequest request,
            @RequestPart("file") MultipartFile file) {

        ProductResponse response = productService.addProduct(request, file);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "Product added successfully.",
                        response));
    }

    /**
     * Get Product By Id
     */
    @GetMapping("/get")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @RequestParam String productId) {

        ProductResponse response =
                productService.getProductById(productId);

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Product fetched successful",
                response));
    }

    /**
     * Get All Products
     */
    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {

        List<ProductResponse> response =
                productService.getAllProducts();

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "All Products fetched  successful.",
                response));
    }

    /**
     * Update Product
     */
    @PatchMapping(value = "/update",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @RequestParam String productId,
            @Valid @RequestPart("request") ProductRequest request,
            @RequestPart(value = "file",required = false) MultipartFile file) {

        ProductResponse response =
                productService.updateProduct(productId, request,file);

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "product update successful.",
                response));
    }

    /**
     * Delete Product
     */
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<ProductResponse>> deleteProduct(
            @RequestParam String productId) {

       ProductResponse response= productService.deleteProduct(productId);

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Product deleted successfully.",
                response
        ));
    }
}
