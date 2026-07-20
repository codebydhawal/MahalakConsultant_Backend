package com.mahalak.media.framework;

import com.mahalak.media.IServices.IGoogleSheetService;
import com.mahalak.media.annotations.SheetColumn;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class GoogleIdGenerator {

    private final IGoogleSheetService googleSheetService;

    public <T> String generate(Class<T> clazz) {

        try {

            String sheetName = ReflectionUtil.getSheetName(clazz);

            Field idField = ReflectionUtil.getIdField(clazz);

            SheetColumn sheetColumn =
                    idField.getAnnotation(SheetColumn.class);

            String prefix = sheetColumn.prefix();

            if (prefix == null || prefix.isBlank()) {
                throw new RuntimeException(
                        "Prefix is not defined for "
                                + clazz.getSimpleName());
            }

            List<List<Object>> rows =
                    googleSheetService.getAllRows(sheetName);

            int idColumnIndex =
                    ReflectionUtil.getSheetFields(clazz)
                            .indexOf(idField);

            int maxNumber = 0;

            Pattern pattern =
                    Pattern.compile("^" + Pattern.quote(prefix) + "(\\d+)$");

            // Skip Header Row
            for (int i = 1; i < rows.size(); i++) {

                List<Object> row = rows.get(i);

                if (row.size() <= idColumnIndex) {
                    continue;
                }

                Object value = row.get(idColumnIndex);

                if (value == null) {
                    continue;
                }

                Matcher matcher =
                        pattern.matcher(value.toString().trim());

                if (matcher.matches()) {

                    int current =
                            Integer.parseInt(matcher.group(1));

                    if (current > maxNumber) {
                        maxNumber = current;
                    }
                }
            }

            return String.format("%s%04d",
                    prefix,
                    maxNumber + 1);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate ID for "
                            + clazz.getSimpleName(), e);
        }
    }
}