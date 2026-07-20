package com.mahalak.media.mapper;

import com.mahalak.media.dto.request.ProductRequest;
import com.mahalak.media.dto.response.ProductResponse;
import com.mahalak.media.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    /**
     * ProductRequest -> Product
     */
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "imageName", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "imageFileId", ignore = true)
    @Mapping(target = "isProductDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductRequest request);

    /**
     * Product -> ProductResponse
     */
    ProductResponse toResponse(Product product);

    /**
     * Update Existing Product
     */
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "imageName", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "imageFileId", ignore = true)
    @Mapping(target = "isProductDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ProductRequest request,
                      @MappingTarget Product product);
}
