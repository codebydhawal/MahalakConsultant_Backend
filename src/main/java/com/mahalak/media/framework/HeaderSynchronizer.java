package com.mahalak.media.framework;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HeaderSynchronizer {

    private final Sheets sheetsService;

    private final SheetMetadataUtil sheetMetadataUtil;

    @Value("${google.sheet.spreadsheet-id}")
    private String spreadsheetId;

    /**
     * Synchronize Entity Header with Google Sheet
     */
    public void synchronize(Class<?> entityClass) throws IOException {

        String sheetName = ReflectionUtil.getSheetName(entityClass);

        ensureSheetExists(sheetName);

        List<String> entityHeaders =
                ReflectionUtil.getHeaders(entityClass);

        List<Object> sheetHeaders =
                readHeader(sheetName);

        List<Request> requests = new ArrayList<>();

        // Add Missing Columns
        if (sheetHeaders.size() < entityHeaders.size()) {

            int columnsToAdd =
                    entityHeaders.size() - sheetHeaders.size();

            SheetMetadata metadata =
                    sheetMetadataUtil.getMetadata(sheetName);

            Integer sheetId = metadata.getSheetId();
            int lastColumn = metadata.getTotalColumns();

            InsertDimensionRequest insertDimension =
                    new InsertDimensionRequest()
                            .setRange(
                                    new DimensionRange()
                                            .setSheetId(sheetId)
                                            .setDimension("COLUMNS")
                                            .setStartIndex(lastColumn)
                                            .setEndIndex(lastColumn + columnsToAdd)
                            )
                            .setInheritFromBefore(true);

            Request request =
                    new Request()
                            .setInsertDimension(insertDimension);

            BatchUpdateSpreadsheetRequest batchRequest =
                    new BatchUpdateSpreadsheetRequest()
                            .setRequests(List.of(request));

            sheetsService.spreadsheets()
                    .batchUpdate(spreadsheetId, batchRequest)
                    .execute();
        }

        // Always synchronize header names
        writeHeader(sheetName, entityHeaders);
    }

    /**
     * Create Sheet if it doesn't exist
     */
    private void ensureSheetExists(String sheetName) throws IOException {

        Spreadsheet spreadsheet = sheetMetadataUtil.getSpreadsheetMetadata();

        for (Sheet sheet : spreadsheet.getSheets()) {

            if (sheet.getProperties()
                    .getTitle()
                    .equalsIgnoreCase(sheetName)) {

                return;
            }
        }

        AddSheetRequest addSheet =
                new AddSheetRequest()
                        .setProperties(
                                new SheetProperties()
                                        .setTitle(sheetName));

        Request request =
                new Request().setAddSheet(addSheet);

        BatchUpdateSpreadsheetRequest body =
                new BatchUpdateSpreadsheetRequest()
                        .setRequests(List.of(request));

        sheetsService.spreadsheets()
                .batchUpdate(spreadsheetId, body)
                .execute();
        sheetMetadataUtil.invalidateCache();
    }

    /**
     * Read Header Row
     */
    private List<Object> readHeader(String sheetName)
            throws IOException {

        ValueRange response =
                sheetsService.spreadsheets()
                        .values()
                        .get(spreadsheetId, sheetName + "!1:1")
                        .execute();

        if (response.getValues() == null
                || response.getValues().isEmpty()) {

            return new ArrayList<>();
        }

        return new ArrayList<>(response.getValues().get(0));
    }

    /**
     * Write Header
     */
    private void writeHeader(String sheetName,
                             List<String> headers)
            throws IOException {

        ValueRange valueRange =
                new ValueRange()
                        .setValues(
                                List.of(new ArrayList<>(headers)));

        sheetsService.spreadsheets()
                .values()
                .update(
                        spreadsheetId,
                        sheetName + "!1:1",
                        valueRange)
                .setValueInputOption("RAW")
                .execute();
    }

    /**
     * Add One Column
     */
    private Request createColumnRequest(String sheetName)
            throws IOException {

        SheetMetadata metadata =
                sheetMetadataUtil.getMetadata(sheetName);

        Integer sheetId = metadata.getSheetId();
        int lastColumn = metadata.getTotalColumns();

        InsertDimensionRequest insertDimension =
                new InsertDimensionRequest()
                        .setRange(
                                new DimensionRange()
                                        .setSheetId(sheetId)
                                        .setDimension("COLUMNS")
                                        .setStartIndex(lastColumn)
                                        .setEndIndex(lastColumn + 1))
                        .setInheritFromBefore(true);

        return new Request()
                .setInsertDimension(insertDimension);
    }
}
