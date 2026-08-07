package com.mahalak.media.entity;

import com.mahalak.media.annotations.Sheet;
import com.mahalak.media.annotations.SheetColumn;
import com.mahalak.media.enums.AddressType;
import com.mahalak.media.framework.CacheableEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Sheet(name = "Address")
@CacheableEntity(ttl = 600)
public class Address {

    @SheetColumn(name = "Address_Id", id = true, prefix = "AD", order = 1)
    private String addressId;

    @SheetColumn(name = "User_Id", order = 2)
    private String userId;

    @SheetColumn(name = "Alternate_Phone_Number", order =3)
    private String alternatePhoneNumber;

    @SheetColumn(name = "Address_Line_1", order = 4)
    private String addressLine1;

    @SheetColumn(name = "Address_Line_2", order = 5)
    private String addressLine2;

    @SheetColumn(name = "Landmark", order = 6)
    private String landmark;

    @SheetColumn(name = "City", order = 7)
    private String city;

    @SheetColumn(name = "State", order = 8)
    private String state;

    @SheetColumn(name = "Country", order = 9)
    private String country;

    @SheetColumn(name = "Postal_Code", order = 10)
    private String postalCode;

    @SheetColumn(name = "Address_Type", order = 11)
    private AddressType addressType;

    @SheetColumn(name = "Is_Default_Address", order = 12)
    private Boolean defaultAddress;

    @SheetColumn(name = "Is_Address_Deleted", order = 13)
    private Boolean isAddressDeleted;

    @SheetColumn(name = "Created_At", order = 14)
    private LocalDateTime createdAt;

    @SheetColumn(name = "Updated_At", order = 15)
    private LocalDateTime updatedAt;
}
