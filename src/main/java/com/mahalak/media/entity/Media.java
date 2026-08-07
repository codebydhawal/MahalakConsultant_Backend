package com.mahalak.media.entity;

import com.mahalak.media.annotations.Sheet;
import com.mahalak.media.annotations.SheetColumn;
import com.mahalak.media.framework.CacheableEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Sheet(name = "Media")
@CacheableEntity(ttl = 600)
public class Media {

    @SheetColumn(name = "Media_Id", id = true, prefix = "MD", order = 1)
    private String mediaId;

    @SheetColumn(name = "Title", order = 2)
    private String title;

    @SheetColumn(name = "Video_Url", order = 3)
    private String videoUrl;

    @SheetColumn(name = "Thumbnail_Image_Name", order = 4)
    private String thumbnailImageName;

    @SheetColumn(name = "Thumbnail_Image_Url", order = 5)
    private String thumbnailImageUrl;

    @SheetColumn(name = "Thumbnail_Image_File_Id", order = 6)
    private String thumbnailImageFileId;

    @SheetColumn(name = "Display_Order", order = 7)
    private Integer displayOrder;

    @SheetColumn(name = "Is_Media_Deleted", order = 8)
    private Boolean isMediaDeleted;

    @SheetColumn(name = "Created_At", order = 9)
    private LocalDateTime createdAt;

    @SheetColumn(name = "Updated_At", order = 10)
    private LocalDateTime updatedAt;
}
