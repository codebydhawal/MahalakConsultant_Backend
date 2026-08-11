package com.mahalak.media.mapper;

import com.mahalak.media.dto.request.ShippingRuleRequest;
import com.mahalak.media.dto.response.ShippingRuleResponse;
import com.mahalak.media.entity.ShippingRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ShippingRuleMapper {

    @Mapping(target = "shippingId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ShippingRule toEntity(ShippingRuleRequest request);

    ShippingRuleResponse toResponse(ShippingRule shippingRule);

    @Mapping(target = "shippingId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ShippingRuleRequest request, @MappingTarget ShippingRule shippingRule);
}
