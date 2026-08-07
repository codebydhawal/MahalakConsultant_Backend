package com.mahalak.media.entity;

import com.mahalak.media.annotations.Sheet;
import com.mahalak.media.annotations.SheetColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Sheet(name = "UserInfo")
public class UserInfo {

    @SheetColumn(name = "UserInfo_Id", id = true, prefix = "UIF", order = 1)
    private String userInfoId;

    @SheetColumn(name = "User_Id", order = 2)
    private String userId;

    @SheetColumn(name = "Profile_Image_Name", order = 3)
    private String profileImageName;

    @SheetColumn(name = "Profile_Image_Url", order = 4)
    private String profileImageUrl;

    @SheetColumn(name = "Profile_Image_File_Id", order = 5)
    private String profileImageFileId;

}