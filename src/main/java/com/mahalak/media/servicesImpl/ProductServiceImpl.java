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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
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
    public ProductResponse updateProduct(
            String productId,
            ProductRequest request,
            MultipartFile file) {

        Product product = entityManager.findById(Product.class, productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found with id : " + productId
                        ));

        if (request == null && (file == null || file.isEmpty())) {
            throw new BadRequestException("Provide product details or an image to update.");
        }

        // Store old image information BEFORE replacing it
        String oldImageFileId = product.getImageFileId();

        // Update normal product fields
        if (request != null) {
            productMapper.updateEntity(request, product);
        }

        String newImageFileId = null;

        // Update image only if a new file is provided
        if (file != null && !file.isEmpty()) {

            String originalName = file.getOriginalFilename();
            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(
                        originalName.lastIndexOf(".")
                );
            }

            String generatedFileName =
                    "PRD_" + UUID.randomUUID() + extension;

            // Upload NEW image
            FileUploadResponseDto driveFile =
                    googleDriveService.upload(
                            file,
                            generatedFileName
                    );

            newImageFileId = driveFile.getFileId();

            product.setImageName(driveFile.getFileName());
            product.setImageFileId(driveFile.getFileId());
            product.setImageUrl(driveFile.getDownloadUrl());
        }

        product.setUpdatedAt(LocalDateTime.now());

        try {

            // Save product with new image information
            entityManager.update(product);

        } catch (Exception exception) {

            // Product update failed.
            // Delete the newly uploaded image so there is no orphan file.
            if (newImageFileId != null && !newImageFileId.isBlank()) {

                try {
                    googleDriveService.delete(newImageFileId);

                } catch (Exception cleanupException) {

                    log.error(
                            "Failed to delete newly uploaded product image. FileId: {}",
                            newImageFileId,
                            cleanupException
                    );
                }
            }

            throw exception;
        }

        // Delete OLD image only after product update succeeds
        if (file != null
                && !file.isEmpty()
                && oldImageFileId != null
                && !oldImageFileId.isBlank()
                && !oldImageFileId.equals(newImageFileId)) {

            try {

                googleDriveService.delete(oldImageFileId);

                log.info(
                        "Old product image deleted successfully. FileId: {}",
                        oldImageFileId
                );

            } catch (Exception exception) {

                // Do NOT delete the new image.
                // Product is already pointing to the new image.
                log.error(
                        "Failed to delete old product image. FileId: {}",
                        oldImageFileId,
                        exception
                );
            }
        }

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
