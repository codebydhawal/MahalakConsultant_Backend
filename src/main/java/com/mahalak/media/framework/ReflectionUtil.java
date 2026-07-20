package com.mahalak.media.framework;

import com.mahalak.media.annotations.Sheet;
import com.mahalak.media.annotations.SheetColumn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ReflectionUtil {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ReflectionUtil() {
    }

    /**
     * Returns Google Sheet Name from Entity
     */
    public static String getSheetName(Class<?> clazz) {

        if (!clazz.isAnnotationPresent(Sheet.class)) {
            throw new RuntimeException(
                    clazz.getSimpleName() + " is not annotated with @Sheet");
        }

        return clazz.getAnnotation(Sheet.class).name();
    }

    /**
     * Returns all Sheet Fields
     */
    public static List<Field> getSheetFields(Class<?> clazz) {

        List<Field> fields = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {

            if (!field.isAnnotationPresent(SheetColumn.class)) {
                continue;
            }

            SheetColumn column = field.getAnnotation(SheetColumn.class);

            if (column.ignore()) {
                continue;
            }

            field.setAccessible(true);

            fields.add(field);
        }

        fields.sort(Comparator.comparingInt(
                field -> field.getAnnotation(SheetColumn.class).order()));

        return fields;
    }

    /**
     * Returns Google Sheet Header
     */
    public static List<String> getHeaders(Class<?> clazz) {

        List<String> headers = new ArrayList<>();

        for (Field field : getSheetFields(clazz)) {

            SheetColumn column =
                    field.getAnnotation(SheetColumn.class);

            headers.add(column.name());
        }

        return headers;
    }

    /**
     * Returns ID Field
     */
    public static Field getIdField(Class<?> clazz) {

        for (Field field : getSheetFields(clazz)) {

            SheetColumn column =
                    field.getAnnotation(SheetColumn.class);

            if (column.id()) {

                field.setAccessible(true);

                return field;
            }
        }

        throw new RuntimeException(
                "No @SheetColumn(id=true) found in "
                        + clazz.getSimpleName());
    }

    /**
     * Convert Entity -> Row
     */
    public static List<Object> toRow(Object entity) {

        List<Object> row = new ArrayList<>();

//        DateTimeFormatter formatter =
//                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {

            for (Field field : getSheetFields(entity.getClass())) {

                Object value = field.get(entity);

                if (value == null) {

                    row.add("");

                } else if (value instanceof LocalDateTime dateTime) {

                    row.add(dateTime.format(FORMATTER));

                } else if (value instanceof LocalDate date) {

                    row.add(date.toString());

                } else {

                    row.add(value);
                }
            }

        } catch (IllegalAccessException e) {

            throw new RuntimeException(e);
        }

        return row;
    }

    /**
     * Convert Row -> Entity
     */
    public static <T> T toEntity(Class<T> clazz,
                                 List<Object> row) {

        try {

            T entity = clazz.getDeclaredConstructor().newInstance();

            List<Field> fields = getSheetFields(clazz);

            for (int i = 0; i < fields.size() && i < row.size(); i++) {

                Field field = fields.get(i);

                Object value = row.get(i);

                field.set(entity, convert(value, field.getType()));
            }

            return entity;

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    /**
     * Convert Google Value to Java Type
     */
    private static Object convert(Object value,
                                  Class<?> type) {

        if (value == null) {
            return null;
        }

        String str = value.toString();

        if (type == String.class) {
            return str;
        }

        if (type == Integer.class || type == int.class) {
            return Integer.parseInt(str);
        }

        if (type == Long.class || type == long.class) {
            return Long.parseLong(str);
        }

        if (type == Double.class || type == double.class) {
            return Double.parseDouble(str);
        }

        if (type == Boolean.class || type == boolean.class) {
            return Boolean.parseBoolean(str);
        }

        if (type == LocalDateTime.class) {
            return LocalDateTime.parse(str, FORMATTER);
        }

        if (type == LocalDate.class) {
            return LocalDate.parse(str);
        }

        return str;
    }

}
