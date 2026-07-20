package com.mahalak.media.framework;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.GridProperties;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SheetMetadataUtil {

    private final Sheets sheetsService;

    @Value("${google.sheet.spreadsheet-id}")
    private String spreadsheetId;

    /**
     * Returns metadata of a Google Sheet tab.
     */
    public SheetMetadata getMetadata(String sheetName) throws IOException {

        Spreadsheet spreadsheet = sheetsService.spreadsheets()
                .get(spreadsheetId)
                .execute();

        for (Sheet sheet : spreadsheet.getSheets()) {

            if (sheet.getProperties().getTitle().equalsIgnoreCase(sheetName)) {

                GridProperties grid = sheet.getProperties().getGridProperties();

                return new SheetMetadata(
                        sheet.getProperties().getSheetId(),
                        grid.getRowCount(),
                        grid.getColumnCount()
                );
            }
        }

        throw new RuntimeException(
                "Sheet '" + sheetName + "' not found.");
    }

}
