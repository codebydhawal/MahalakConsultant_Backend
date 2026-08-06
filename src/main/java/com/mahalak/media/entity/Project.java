package com.mahalak.media.entity;

import com.mahalak.media.annotations.Sheet;
import com.mahalak.media.annotations.SheetColumn;
import com.mahalak.media.enums.ProjectCategory;
import com.mahalak.media.framework.CacheableEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Sheet(name = "Projects")
@CacheableEntity(ttl = 300)
public class Project {

    @SheetColumn(name = "Project_Id", id = true, prefix = "PRJ", order = 1)
    private String projectId;

    @SheetColumn(name = "Title", order = 2)
    private String title;

    @SheetColumn(name = "Category", order = 3)
    private ProjectCategory category;

    @SheetColumn(name = "Short_Description", order = 4)
    private String shortDescription;

    @SheetColumn(name = "Thumbnail_Name", order = 5)
    private String thumbnailName;

    @SheetColumn(name = "Thumbnail_Url", order = 6)
    private String thumbnailUrl;

    @SheetColumn(name = "Thumbnail_File_Id", order = 7)
    private String thumbnailFileId;

    @SheetColumn(name = "Document_Name", order = 8)
    private String documentName;

    @SheetColumn(name = "Document_Url", order = 9)
    private String documentUrl;

    @SheetColumn(name = "Document_File_Id", order = 10)
    private String documentFileId;

    @SheetColumn(name = "Client_Name", order = 11)
    private String clientName;

    @SheetColumn(name = "Location", order = 12)
    private String location;

    @SheetColumn(name = "Completion_Date", order = 13)
    private String completionDate;

    @SheetColumn(name = "Project_Area", order = 14)
    private String projectArea;

    @SheetColumn(name = "Views", order = 15)
    private Long views;

    @SheetColumn(name = "Is_Project_Deleted", order = 16)
    private Boolean isProjectDeleted;

    @SheetColumn(name = "Created_At", order = 17)
    private LocalDateTime createdAt;

    @SheetColumn(name = "Updated_At", order = 18)
    private LocalDateTime updatedAt;
}
