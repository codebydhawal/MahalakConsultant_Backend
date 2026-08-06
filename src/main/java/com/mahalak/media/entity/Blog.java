package com.mahalak.media.entity;

import com.mahalak.media.annotations.Sheet;
import com.mahalak.media.annotations.SheetColumn;
import com.mahalak.media.enums.BlogStatus;
import com.mahalak.media.framework.CacheableEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Sheet(name = "Blogs")
@CacheableEntity(ttl = 300)
public class Blog {

    @SheetColumn(name = "Blog_Id", id = true, prefix = "BLG", order = 1)
    private String id;

    @SheetColumn(name = "Title", order = 2)
    private String title;

    @SheetColumn(name = "AuthorName", order = 3)
    private String authorName;

    @SheetColumn(name = "ShortDescription", order = 4)
    private String shortDescription;

    @SheetColumn(name = "Category", order = 5)
    private String category;

    @SheetColumn(name = "Tags", order = 6)
    private List<String> tags;

    @SheetColumn(name = "PublishDate", order = 7)
    private LocalDate publishDate;

    @SheetColumn(name = "Status", order = 8)
    private BlogStatus status;

    @SheetColumn(name = "Views", order = 9)
    private Integer views;

    /* ==========================
       Featured Thumbnail
       ========================== */

    @SheetColumn(name = "FeaturedImageName", order = 10)
    private String featuredImageName;

    @SheetColumn(name = "FeaturedImageUrl", order = 11)
    private String featuredImageUrl;

    @SheetColumn(name = "FeaturedImageFileId", order = 12)
    private String featuredImageFileId;

    /* ==========================
       Blog Content PDF
       ========================== */

    @SheetColumn(name = "ContentFileName", order = 13)
    private String contentFileName;

    @SheetColumn(name = "ContentFileUrl", order = 14)
    private String contentFileUrl;

    @SheetColumn(name = "ContentFileId", order = 15)
    private String contentFileId;

    /* ==========================
       Author Image (Optional)
       ========================== */

    @SheetColumn(name = "AuthorImageName", order = 16)
    private String authorImageName;

    @SheetColumn(name = "AuthorImageUrl", order = 17)
    private String authorImageUrl;

    @SheetColumn(name = "AuthorImageFileId", order = 18)
    private String authorImageFileId;

    /* ==========================
       Audit Information
       ========================== */

    @SheetColumn(name = "IsBlogDeleted", order = 19)
    private Boolean isBlogDeleted;

    @SheetColumn(name = "CreatedAt", order = 20)
    private LocalDateTime createdAt;

    @SheetColumn(name = "UpdatedAt", order = 21)
    private LocalDateTime updatedAt;
}