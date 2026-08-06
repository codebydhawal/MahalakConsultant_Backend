package com.mahalak.media.mapper;

import com.mahalak.media.dto.request.CartRequest;
import com.mahalak.media.dto.response.CartResponse;
import com.mahalak.media.dto.response.CartSummaryResponse;
import com.mahalak.media.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "cartId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Cart toEntity(CartRequest request);

    CartSummaryResponse toResponse(Cart cart);

}
