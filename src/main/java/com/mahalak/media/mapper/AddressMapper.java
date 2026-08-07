package com.mahalak.media.mapper;

import com.mahalak.media.dto.request.AddressRequest;
import com.mahalak.media.dto.response.AddressResponse;
import com.mahalak.media.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    @Mapping(target = "addressId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "isAddressDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Address toEntity(AddressRequest request);

    AddressResponse toResponse(Address address);

    @Mapping(target = "addressId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "isAddressDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Address updateEntity(AddressRequest request, @MappingTarget Address address);
}
