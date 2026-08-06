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
@Sheet(name = "Team")
@CacheableEntity(ttl = 600)
public class Team {

    @SheetColumn(name = "Team_Id", id = true, prefix = "TM", order = 1)
    private String teamId;

    @SheetColumn(name = "Full_Name", order = 2)
    private String fullName;

    @SheetColumn(name = "Designation", order = 3)
    private String designation;

    @SheetColumn(name = "Short_Bio", order = 4)
    private String shortBio;

    @SheetColumn(name = "Profile_Image_Name", order = 5)
    private String profileImageName;

    @SheetColumn(name = "Profile_Image_Url", order = 6)
    private String profileImageUrl;

    @SheetColumn(name = "Profile_Image_File_Id", order = 7)
    private String profileImageFileId;

    @SheetColumn(name = "Email", order = 8)
    private String email;

    @SheetColumn(name = "Phone_Number", order = 9)
    private String phoneNumber;

    @SheetColumn(name = "LinkedIn_Url", order = 10)
    private String linkedInUrl;

    @SheetColumn(name = "Instagram_Url", order = 11)
    private String instagramUrl;

    @SheetColumn(name = "Facebook_Url", order = 12)
    private String facebookUrl;

    @SheetColumn(name = "Twitter_Url", order = 13)
    private String twitterUrl;

    @SheetColumn(name = "Display_Order", order = 14)
    private Integer displayOrder;

    @SheetColumn(name = "Is_Team_Deleted", order = 15)
    private Boolean isTeamDeleted;

    @SheetColumn(name = "Created_At", order = 16)
    private LocalDateTime createdAt;

    @SheetColumn(name = "Updated_At", order = 17)
    private LocalDateTime updatedAt;
}
