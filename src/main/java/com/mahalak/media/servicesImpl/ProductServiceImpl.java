package com.mahalak.media.servicesImpl;

import com.mahalak.media.IServices.GoogleDriveService;
import com.mahalak.media.IServices.IProductService;
import com.mahalak.media.dto.request.ProductRequest;
import com.mahalak.media.dto.response.ProductResponse;
import com.mahalak.media.dto.wrapper.FileUploadResponseDto;
import com.mahalak.media.entity.Product;
import com.mahalak.media.exception.BadRequestException;
import com.mahalak.media.framework.GoogleEntityManager;
import com.mahalak.media.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final GoogleEntityManager entityManager;

    private final ProductMapper productMapper;

    private final GoogleDriveService googleDriveService;

    @Override
    public ProductResponse addProduct(ProductRequest request, MultipartFile file) {

        // Check if product already exists
        Optional<Product> existingProduct =
                entityManager.findAll(Product.class)
                        .stream()
                        .filter(product -> !Boolean.TRUE.equals(product.getIsProductDeleted()))
                        .filter(product -> product.getName().equalsIgnoreCase(request.getName()))
                        .findFirst();

        if (existingProduct.isPresent()) {
            throw new BadRequestException(
                    "Product with name '" + request.getName() + "' already exists."
            );
        }

        Product product = productMapper.toEntity(request);

        // Upload image if provided
        if (file != null && !file.isEmpty()) {

            String originalName = file.getOriginalFilename();
            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            String generatedFileName = "PRD_" + UUID.randomUUID() + extension;

            FileUploadResponseDto driveFile =
                    googleDriveService.upload(file, generatedFileName);

            product.setImageName(driveFile.getFileName());
            product.setImageFileId(driveFile.getFileId());
            product.setImageUrl(driveFile.getDownloadUrl());
        }

        product.setIsProductDeleted(false);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        entityManager.save(product);

        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse getProductById(String productId) {

        Product product = entityManager.findById(Product.class, productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found with id : " + productId));

        return productMapper.toResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {

        return entityManager.findAll(Product.class)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponse updateProduct(String productId,
                                         ProductRequest request,
                                         MultipartFile file) {

        Product product = entityManager.findById(Product.class, productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found with id : " + productId));

        // Update normal fields
        productMapper.updateEntity(request, product);

        // Update image if a new file is provided
        if (file != null && !file.isEmpty()) {

            String originalName = file.getOriginalFilename();
            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            String generatedFileName = "PRD_" + UUID.randomUUID() + extension;

            // Upload new image
            FileUploadResponseDto driveFile =
                    googleDriveService.upload(file, generatedFileName);

            product.setImageName(driveFile.getFileName());
            product.setImageFileId(driveFile.getFileId());
            product.setImageUrl(driveFile.getDownloadUrl());
        }

        product.setUpdatedAt(LocalDateTime.now());

        entityManager.update(product);

        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse deleteProduct(String productId) {

        Product product = entityManager.findById(Product.class, productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found with id : " + productId));

        if (Boolean.TRUE.equals(product.getIsProductDeleted())) {
            throw new BadRequestException("Product is already deleted.");
        }

        product.setIsProductDeleted(true);
        product.setUpdatedAt(LocalDateTime.now());

        entityManager.update(product);

        return productMapper.toResponse(product);
    }
}
