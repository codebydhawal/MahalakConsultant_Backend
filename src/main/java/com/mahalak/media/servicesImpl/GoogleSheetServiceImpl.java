package com.mahalak.media.servicesImpl;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.mahalak.media.IServices.IGoogleSheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleSheetServiceImpl implements IGoogleSheetService {

    private final Sheets sheets;

    @Value("${google.sheet.spreadsheet-id}")
    private String spreadsheetId;

    /**
     * Append New Row
     */
    @Override
    public void appendRow(String sheetName, List<Object> rowData)
            throws IOException, GeneralSecurityException {

        ValueRange body = new ValueRange()
                .setValues(Collections.singletonList(rowData));

        sheets.spreadsheets().values()
                .append(
                        spreadsheetId,
                        sheetName,
                        body
                )
                .setValueInputOption("RAW")
                .execute();
    }

    /**
     * Read All Rows
     */
    @Override
    public List<List<Object>> getAllRows(String sheetName)
            throws IOException {

        ValueRange response = sheets.spreadsheets()
                .values()
                .get(spreadsheetId, sheetName)
                .execute();

        return response.getValues();
    }

    /**
     * Find Row By Id
     */
    @Override
    public List<Object> getRowById(String sheetName, String id)
            throws IOException {

        List<List<Object>> rows = getAllRows(sheetName);

        if (rows == null || rows.size() <= 1) {
            return null;
        }

        for (int i = 1; i < rows.size(); i++) {

            List<Object> row = rows.get(i);

            if (!row.isEmpty()
                    && id.equalsIgnoreCase(row.get(0).toString())) {

                return row;
            }
        }

        return null;
    }

    /**
     * Update Existing Row
     */
    @Override
    public void updateRow(String sheetName,
                          String id,
                          List<Object> rowData)
            throws IOException {

        List<List<Object>> rows = getAllRows(sheetName);

        if (rows == null || rows.size() <= 1) {
            return;
        }

        for (int i = 1; i < rows.size(); i++) {

            List<Object> row = rows.get(i);

            if (!row.isEmpty()
                    && id.equalsIgnoreCase(row.get(0).toString())) {

                String range = sheetName + "!A" + (i + 1);

                ValueRange body = new ValueRange()
                        .setValues(Collections.singletonList(rowData));

                sheets.spreadsheets()
                        .values()
                        .update(
                                spreadsheetId,
                                range,
                                body
                        )
                        .setValueInputOption("RAW")
                        .execute();

                return;
            }
        }
    }

    /**
     * Delete Row
     */
    @Override
    public void deleteRow(String sheetName,
                          String id)
            throws IOException {

        List<List<Object>> rows = getAllRows(sheetName);

        if (rows == null || rows.size() <= 1) {
            return;
        }

        for (int i = 1; i < rows.size(); i++) {

            List<Object> row = rows.get(i);

            if (!row.isEmpty()
                    && id.equalsIgnoreCase(row.get(0).toString())) {

                DeleteDimensionRequest deleteRequest =
                        new DeleteDimensionRequest()
                                .setRange(
                                        new DimensionRange()
                                                .setSheetId(getSheetId(sheetName))
                                                .setDimension("ROWS")
                                                .setStartIndex(i)
                                                .setEndIndex(i + 1)
                                );

                BatchUpdateSpreadsheetRequest batchRequest =
                        new BatchUpdateSpreadsheetRequest()
                                .setRequests(
                                        Collections.singletonList(
                                                new Request()
                                                        .setDeleteDimension(deleteRequest)
                                        )
                                );

                sheets.spreadsheets()
                        .batchUpdate(spreadsheetId, batchRequest)
                        .execute();

                return;
            }
        }
    }

    /**
     * Get Google Sheet Id
     */
    private Integer getSheetId(String sheetName)
            throws IOException {

        return sheets.spreadsheets()
                .get(spreadsheetId)
                .execute()
                .getSheets()
                .stream()
                .filter(sheet ->
                        sheet.getProperties()
                                .getTitle()
                                .equalsIgnoreCase(sheetName))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Sheet not found : " + sheetName))
                .getProperties()
                .getSheetId();
    }
}