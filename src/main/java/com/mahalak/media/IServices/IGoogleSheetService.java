package com.mahalak.media.IServices;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface IGoogleSheetService {

    /**
     * Append a new row into the given sheet.
     *
     * @param sheetName Google Sheet tab name (Products, Blogs, Users...)
     * @param rowData   Row values to insert
     */
    void appendRow(String sheetName, List<Object> rowData)
            throws IOException, GeneralSecurityException;

    /**
     * Get all rows from the given sheet.
     *
     * @param sheetName Google Sheet tab name
     * @return List of rows
     */
    List<List<Object>> getAllRows(String sheetName)
            throws IOException, GeneralSecurityException;

    /**
     * Find a row using the unique ID stored in first column.
     *
     * @param sheetName Google Sheet tab name
     * @param id        ProductId / BlogId / UserId ...
     * @return Matching row or null
     */
    List<Object> getRowById(String sheetName, String id)
            throws IOException, GeneralSecurityException;

    /**
     * Update an existing row.
     *
     * @param sheetName Google Sheet tab name
     * @param id        Unique ID
     * @param rowData   Updated values
     */
    void updateRow(String sheetName, String id, List<Object> rowData)
            throws IOException, GeneralSecurityException;

    /**
     * Delete a row using its unique ID.
     *
     * @param sheetName Google Sheet tab name
     * @param id        Unique ID
     */
    void deleteRow(String sheetName, String id)
            throws IOException, GeneralSecurityException;

}
