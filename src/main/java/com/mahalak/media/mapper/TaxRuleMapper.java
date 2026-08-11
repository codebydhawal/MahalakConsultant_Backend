package com.mahalak.media.mapper;

import com.mahalak.media.dto.request.TaxRuleRequest;
import com.mahalak.media.dto.response.TaxRuleResponse;
import com.mahalak.media.entity.TaxRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaxRuleMapper {

    @Mapping(target = "taxId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TaxRule toEntity(TaxRuleRequest request);

    TaxRuleResponse toResponse(TaxRule taxRule);

    @Mapping(target = "taxId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(TaxRuleRequest request, @MappingTarget TaxRule taxRule);
}
