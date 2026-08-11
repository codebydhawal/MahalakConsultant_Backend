package com.mahalak.media.entity;

import com.mahalak.media.annotations.Sheet;
import com.mahalak.media.annotations.SheetColumn;
import com.mahalak.media.framework.CacheableEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Sheet(name = "Payment")
@CacheableEntity(ttl = 300)
public class Payment {

    @SheetColumn(name = "Payment_Id", id = true, prefix = "PAY", order = 1)
    private String paymentId;

    @SheetColumn(name = "Order_Id", order = 2)
    private String orderId;

    @SheetColumn(name = "Payment_Method", order = 3)
    private String paymentMethod;

    @SheetColumn(name = "Payment_Status", order = 4)
    private String paymentStatus;

    @SheetColumn(name = "Amount", order = 5)
    private Double amount;

    @SheetColumn(name = "Transaction_Id", order = 6)
    private String transactionId;

    @SheetColumn(name = "Screenshot_Name", order = 7)
    private String screenshotName;

    @SheetColumn(name = "Screenshot_Url", order = 8)
    private String screenshotUrl;

    @SheetColumn(name = "Screenshot_File_Id", order = 9)
    private String screenshotFileId;

    @SheetColumn(name = "Verified_By", order = 10)
    private String verifiedBy;

    @SheetColumn(name = "Verified_At", order = 11)
    private LocalDateTime verifiedAt;

    @SheetColumn(name = "Rejection_Reason", order = 12)
    private String rejectionReason;

    @SheetColumn(name = "Created_At", order = 13)
    private LocalDateTime createdAt;

    @SheetColumn(name = "Updated_At", order = 14)
    private LocalDateTime updatedAt;
}
