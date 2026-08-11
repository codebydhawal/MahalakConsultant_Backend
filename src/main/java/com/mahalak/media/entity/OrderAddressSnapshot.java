package com.mahalak.media.entity;

import com.mahalak.media.annotations.Sheet;
import com.mahalak.media.annotations.SheetColumn;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Sheet(name = "Order_Address_Snapshot")
public class OrderAddressSnapshot {
    @SheetColumn(name = "Snapshot_Id", id = true, prefix = "OAS", order = 1) private String snapshotId;
    @SheetColumn(name = "Order_Id", order = 2) private String orderId;
    @SheetColumn(name = "Recipient_Phone", order = 3) private String recipientPhone;
    @SheetColumn(name = "Address_Line_1", order = 4) private String addressLine1;
    @SheetColumn(name = "Address_Line_2", order = 5) private String addressLine2;
    @SheetColumn(name = "Landmark", order = 6) private String landmark;
    @SheetColumn(name = "City", order = 7) private String city;
    @SheetColumn(name = "State", order = 8) private String state;
    @SheetColumn(name = "Country", order = 9) private String country;
    @SheetColumn(name = "Postal_Code", order = 10) private String postalCode;
    @SheetColumn(name = "Created_At", order = 11) private LocalDateTime createdAt;
}
