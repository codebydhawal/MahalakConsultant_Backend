package com.mahalak.media.mapper;

import com.mahalak.media.dto.request.DiscountRuleRequest;
import com.mahalak.media.dto.response.DiscountRuleResponse;
import com.mahalak.media.entity.DiscountRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DiscountRuleMapper {

    @Mapping(target = "discountId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DiscountRule toEntity(DiscountRuleRequest request);

    DiscountRuleResponse toResponse(DiscountRule discountRule);

    @Mapping(target = "discountId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(DiscountRuleRequest request, @MappingTarget DiscountRule discountRule);
}
