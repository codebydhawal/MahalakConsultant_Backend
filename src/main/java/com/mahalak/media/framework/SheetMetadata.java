package com.mahalak.media.framework;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SheetMetadata {

    private Integer sheetId;

    private Integer totalRows;

    private Integer totalColumns;

}
