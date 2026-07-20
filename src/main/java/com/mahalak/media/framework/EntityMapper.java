package com.mahalak.media.framework;

import java.util.List;

public final class EntityMapper {

    private EntityMapper() {
    }

    public static List<Object> toRow(Object entity) {
        return ReflectionUtil.toRow(entity);
    }

    public static <T> T toEntity(Class<T> clazz, List<Object> row) {
        return ReflectionUtil.toEntity(clazz, row);
    }
}
