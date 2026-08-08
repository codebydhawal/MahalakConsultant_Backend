package com.mahalak.media.controller;

import com.mahalak.media.IServices.IAddressService;
import com.mahalak.media.dto.request.AddressRequest;
import com.mahalak.media.dto.response.AddressResponse;
import com.mahalak.media.dto.wrapper.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/address")
@RequiredArgsConstructor
public class AddressController {

    private final IAddressService addressService;

    /**
     * Add Address
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(

            @Valid @RequestBody AddressRequest request) {

        AddressResponse response =
                addressService.addAddress(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "Address added successfully.",
                        response));
    }

    /**
     * Get Address By Id
     */
    @GetMapping("/get")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(

            @RequestParam String addressId) {

        AddressResponse response =
                addressService.getAddressById(addressId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Address fetched successfully.",
                        response));
    }

    /**
     * Get All Addresses
     */
    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAllAddresses() {

        List<AddressResponse> response =
                addressService.getAllAddresses();

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Addresses fetched successfully.",
                        response));
    }

    /**
     * Get Available Locations
     */
    @GetMapping("/locations")
    public ResponseEntity<ApiResponse<List<String>>> getLocations() {

        List<String> response = addressService.getLocations();

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Locations fetched successfully.",
                        response));
    }

    /**
     * Get Logged In User Addresses
     */
    @GetMapping("/get-by-user")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddressesByUser() {

        List<AddressResponse> response =
                addressService.getAddressesByUser();

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "User addresses fetched successfully.",
                        response));
    }

    /**
     * Update Address
     */
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(

            @RequestParam String addressId,

            @Valid @RequestBody AddressRequest request) {

        AddressResponse response =
                addressService.updateAddress(addressId, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Address updated successfully.",
                        response));
    }

    /**
     * Delete Address
     */
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<AddressResponse>> deleteAddress(

            @RequestParam String addressId) {

        AddressResponse response =
                addressService.deleteAddress(addressId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Address deleted successfully.",
                        response));
    }

    /**
     * Set Default Address
     */
    @PutMapping("/set-default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(

            @RequestParam String addressId) {

        AddressResponse response =
                addressService.setDefaultAddress(addressId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Default address updated successfully.",
                        response));
    }
}