package com.mahalak.media.dto.response;

import com.mahalak.media.enums.AddressType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    private String addressId;

    private String userId;

    private String alternatePhoneNumber;

    private String addressLine1;

    private String addressLine2;

    private String landmark;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    private AddressType addressType;

    private Boolean defaultAddress;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
