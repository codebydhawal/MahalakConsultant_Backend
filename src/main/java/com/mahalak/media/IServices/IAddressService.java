package com.mahalak.media.IServices;

import com.mahalak.media.dto.request.AddressRequest;
import com.mahalak.media.dto.response.AddressResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface IAddressService {
    AddressResponse addAddress(@Valid AddressRequest request);

    AddressResponse getAddressById(String addressId);

    List<AddressResponse> getAllAddresses();

    List<String> getLocations();

    List<AddressResponse> getAddressesByUser();
    List<AddressResponse> getAddressesByUser(Long userId);

    AddressResponse updateAddress(String addressId, @Valid AddressRequest request);

    AddressResponse deleteAddress(String addressId);

    AddressResponse setDefaultAddress(String addressId);
}
