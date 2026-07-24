package com.mahalak.media.framework;

import com.mahalak.media.annotations.Sheet;
import com.mahalak.media.annotations.SheetColumn;

import java.lang.reflect.Field;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

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

        try {

            for (Field field : getSheetFields(entity.getClass())) {

                Object value = field.get(entity);

                if (value == null) {

                    row.add("");

                } else if (value instanceof LocalDateTime dateTime) {

                    row.add(dateTime.format(FORMATTER));

                } else if (value instanceof LocalDate date) {

                    row.add(date.toString());

                } else if (value instanceof Collection<?> collection) {

                    row.add(
                            collection.stream()
                                    .map(Object::toString)
                                    .collect(Collectors.joining(","))
                    );

                } else if (value.getClass().isEnum()) {

                    row.add(value.toString());

                } else {

                    row.add(value.toString());
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

                if (field.getType().equals(List.class)) {

                    if (value == null || value.toString().isBlank()) {

                        field.set(entity, new ArrayList<>());

                    } else {

                        field.set(
                                entity,
                                Arrays.stream(value.toString().split(","))
                                        .map(String::trim)
                                        .filter(s -> !s.isEmpty())
                                        .toList()
                        );

                    }

                } else {

                    field.set(entity, convert(value, field.getType()));

                }
            }

            return entity;

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    /**
     * Convert Google Value to Java Type
     */
    private static Object convert(Object value, Class<?> type) {

        if (value == null) {
            return null;
        }

        String str = value.toString().trim();

        if (type == String.class) {
            return str;
        }

        if (type == Integer.class || type == int.class) {
            return str.isBlank() ? null : Integer.parseInt(str);
        }

        if (type == Long.class || type == long.class) {
            return str.isBlank() ? null : Long.parseLong(str);
        }

        if (type == Double.class || type == double.class) {
            return str.isBlank() ? null : Double.parseDouble(str);
        }

        if (type == Boolean.class || type == boolean.class) {
            return str.isBlank() ? false : Boolean.parseBoolean(str);
        }

        if (type == LocalDateTime.class) {
            return str.isBlank() ? null : LocalDateTime.parse(str, FORMATTER);
        }

        if (type == LocalDate.class) {
            return str.isBlank() ? null : LocalDate.parse(str);
        }

        if (type.isEnum()) {
            return Enum.valueOf((Class<Enum>) type, str);
        }

        if (type == List.class) {
            if (str.isBlank()) {
                return new ArrayList<String>();
            }

            return Arrays.stream(str.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }

        return str;
    }

}
