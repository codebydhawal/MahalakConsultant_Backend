package com.mahalak.media.servicesImpl;

import com.mahalak.media.IServices.GoogleDriveService;
import com.mahalak.media.IServices.IAddressService;
import com.mahalak.media.auth.SecurityUtil;
import com.mahalak.media.dto.request.AddressRequest;
import com.mahalak.media.dto.response.AddressResponse;
import com.mahalak.media.entity.Address;
import com.mahalak.media.entity.Cart;
import com.mahalak.media.exception.BadRequestException;
import com.mahalak.media.exception.ResourceNotFoundException;
import com.mahalak.media.framework.GoogleEntityManager;
import com.mahalak.media.mapper.AddressMapper;
import com.mahalak.media.mapper.MediaMapper;
import com.mahalak.media.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements IAddressService {

    private final GoogleEntityManager entityManager;

    private final AddressMapper addressMapper;

    private final GoogleDriveService googleDriveService;

    private final UserRepository userRepository;

    @Override
    public AddressResponse addAddress(AddressRequest request) {

        Long userId = SecurityUtil.getCurrentUserId();

        Address entity = addressMapper.toEntity(request);

        entity.setUserId(userId.toString());

        Optional<Address> existingDefaultAddress = entityManager.findAll(Address.class)
                .stream()
                .filter(address ->
                        !Boolean.TRUE.equals(address.getIsAddressDeleted()) &&
                                userId.toString().equals(address.getUserId()) &&
                                Boolean.TRUE.equals(address.getDefaultAddress()))
                .findFirst();

        // If the user has no default address, make this one the default.
        if (existingDefaultAddress.isEmpty()) {
            entity.setDefaultAddress(true);
        } else {
            entity.setDefaultAddress(false);
        }

        entity.setIsAddressDeleted(false);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        entityManager.save(entity);

        return addressMapper.toResponse(entity);
    }

    @Override
    public AddressResponse getAddressById(String addressId) {

        Address address = entityManager.findById(Address.class, addressId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found with id : " + addressId));

        if (Boolean.TRUE.equals(address.getIsAddressDeleted())) {
            throw new ResourceNotFoundException(
                    "Address not found with id : " + addressId);
        }

        return addressMapper.toResponse(address);
    }

    @Override
    public List<AddressResponse> getAllAddresses() {

        return entityManager.findAll(Address.class)
                .stream()
                .filter(address ->
                        !Boolean.TRUE.equals(address.getIsAddressDeleted()))
                .sorted(
                        java.util.Comparator
                                .comparing(Address::getDefaultAddress,
                                        java.util.Comparator.reverseOrder())
                                .thenComparing(Address::getUpdatedAt,
                                        java.util.Comparator.reverseOrder())
                )
                .map(addressMapper::toResponse)
                .toList();
    }

    @Override
    public List<AddressResponse> getAddressesByUser() {

        Long userId = SecurityUtil.getCurrentUserId();

        return entityManager.findAll(Address.class)
                .stream()
                .filter(address ->
                        !Boolean.TRUE.equals(address.getIsAddressDeleted()) &&
                                userId.toString().equals(address.getUserId()))
                .sorted(
                        java.util.Comparator
                                .comparing(Address::getDefaultAddress,
                                        java.util.Comparator.reverseOrder())
                                .thenComparing(Address::getUpdatedAt,
                                        java.util.Comparator.reverseOrder())
                )
                .map(addressMapper::toResponse)
                .toList();
    }

    @Override
    public List<AddressResponse> getAddressesByUser(Long userId) {
        return entityManager.findAll(Address.class)
                .stream()
                .filter(address ->
                        !Boolean.TRUE.equals(address.getIsAddressDeleted()) &&
                                userId.toString().equals(address.getUserId()))
                .sorted(
                        java.util.Comparator
                                .comparing(Address::getDefaultAddress,
                                        java.util.Comparator.reverseOrder())
                                .thenComparing(Address::getUpdatedAt,
                                        java.util.Comparator.reverseOrder())
                )
                .map(addressMapper::toResponse)
                .toList();
    }

    @Override
    public AddressResponse updateAddress(
            String addressId,
            AddressRequest request) {

        Long userId = SecurityUtil.getCurrentUserId();

        Address address = entityManager.findById(Address.class, addressId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found with id : " + addressId));

        if (!userId.toString().equals(address.getUserId())) {
            throw new BadRequestException(
                    "You are not authorized to update this address.");
        }

        address = addressMapper.updateEntity(request, address);

        address.setUpdatedAt(LocalDateTime.now());

        entityManager.update(address);

        return addressMapper.toResponse(address);
    }

    @Override
    public AddressResponse deleteAddress(String addressId) {

        Long userId = SecurityUtil.getCurrentUserId();

        Address address = entityManager.findById(Address.class, addressId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found with id : " + addressId));

        if (Boolean.TRUE.equals(address.getIsAddressDeleted())) {
            throw new ResourceNotFoundException(
                    "Address not found with id : " + addressId);
        }

        // Ensure the logged-in user owns this address
        if (!userId.toString().equals(address.getUserId())) {
            throw new BadRequestException(
                    "You are not authorized to delete this address.");
        }

        address.setIsAddressDeleted(true);
        address.setUpdatedAt(LocalDateTime.now());

        entityManager.update(address);

        return addressMapper.toResponse(address);
    }

    @Override
    public AddressResponse setDefaultAddress(String addressId) {

        Long userId = SecurityUtil.getCurrentUserId();

        Address selectedAddress = entityManager.findById(Address.class, addressId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found with id : " + addressId));

        if (Boolean.TRUE.equals(selectedAddress.getIsAddressDeleted())) {
            throw new ResourceNotFoundException(
                    "Address not found with id : " + addressId);
        }

        if (!userId.toString().equals(selectedAddress.getUserId())) {
            throw new BadRequestException(
                    "You are not authorized to update this address.");
        }

        // Remove the current default address
        entityManager.findAll(Address.class)
                .stream()
                .filter(address ->
                        !Boolean.TRUE.equals(address.getIsAddressDeleted()))
                .filter(address ->
                        userId.toString().equals(address.getUserId()))
                .filter(address ->
                        Boolean.TRUE.equals(address.getDefaultAddress()))
                .forEach(address -> {

                    address.setDefaultAddress(false);
                    address.setUpdatedAt(LocalDateTime.now());

                    entityManager.update(address);
                });

        // Make the selected address the new default
        selectedAddress.setDefaultAddress(true);
        selectedAddress.setUpdatedAt(LocalDateTime.now());

        entityManager.update(selectedAddress);

        return addressMapper.toResponse(selectedAddress);
    }
}
