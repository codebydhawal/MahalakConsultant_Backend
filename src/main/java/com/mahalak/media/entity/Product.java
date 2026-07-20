package com.mahalak.media.entity;

import com.mahalak.media.annotations.Sheet;
import com.mahalak.media.annotations.SheetColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Sheet(name = "Products")
public class Product {

    @SheetColumn(name = "Product_Id", id = true, prefix = "PRD", order = 1)
    private String productId;

    @SheetColumn(name = "Name", order = 2)
    private String name;

    @SheetColumn(name = "Description", order = 3)
    private String description;

    @SheetColumn(name = "Category", order = 4)
    private String category;

    @SheetColumn(name = "Price", order = 5)
    private Double price;

    @SheetColumn(name = "Stock", order = 6)
    private Integer stock;

    @SheetColumn(name = "Status", order = 7)
    private String status;

    @SheetColumn(name = "Image_Name", order = 8)
    private String imageName;

    @SheetColumn(name = "Image_Url", order = 9)
    private String imageUrl;

    @SheetColumn(name = "Image_File_Id", order = 10)
    private String imageFileId;

    @SheetColumn(name = "Is_Product_Deleted", order = 11)
    private Boolean isProductDeleted;

    @SheetColumn(name = "Created_At", order = 12)
    private LocalDateTime createdAt;

    @SheetColumn(name = "Updated_At", order = 13)
    private LocalDateTime updatedAt;

}
