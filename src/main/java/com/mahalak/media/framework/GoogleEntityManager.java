package com.mahalak.media.framework;

import com.mahalak.media.IServices.IGoogleSheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GoogleEntityManager {

    private final IGoogleSheetService googleSheetService;

    private final HeaderSynchronizer headerSynchronizer;

    private final GoogleIdGenerator googleIdGenerator;

    /**
     * Save Entity
     */
    public <T> void save(T entity) {

        try {

            // Create Sheet & Synchronize Headers
            headerSynchronizer.synchronize(entity.getClass());

            String sheetName =
                    ReflectionUtil.getSheetName(entity.getClass());

            // Get ID Field
            Field idField =
                    ReflectionUtil.getIdField(entity.getClass());

            idField.setAccessible(true);

            // Generate ID if not present
            Object currentId = idField.get(entity);

            if (currentId == null || currentId.toString().isBlank()) {

                String generatedId =
                        googleIdGenerator.generate((Class<?>) entity.getClass());

                idField.set(entity, generatedId);
            }

            // Create Row AFTER ID generation
            List<Object> row =
                    ReflectionUtil.toRow(entity);

            // Save Row
            googleSheetService.appendRow(sheetName, row);

        } catch (Exception e) {

            throw new RuntimeException("Failed to save entity.", e);
        }
    }

    /**
     * Find All
     */
    public <T> List<T> findAll(Class<T> clazz) {

        try {

            String sheetName =
                    ReflectionUtil.getSheetName(clazz);

            List<List<Object>> rows =
                    googleSheetService.getAllRows(sheetName);

            return rows.stream()
                    .skip(1) // Skip Header Row
                    .map(row -> ReflectionUtil.toEntity(clazz, row))
                    .toList();

        } catch (Exception e) {

            throw new RuntimeException("Failed to fetch entities.", e);
        }
    }

    /**
     * Find By Id
     */
    public <T> Optional<T> findById(Class<T> clazz,
                                    Object id) {

        try {

            Field idField =
                    ReflectionUtil.getIdField(clazz);

            return findAll(clazz)
                    .stream()
                    .filter(entity -> {

                        try {

                            Object value =
                                    idField.get(entity);

                            return value != null &&
                                    value.equals(id);

                        } catch (Exception e) {

                            return false;
                        }

                    }).findFirst();

        } catch (Exception e) {

            throw new RuntimeException("Failed to fetch entity.", e);
        }
    }

    /**
     * Update Entity
     */
    public <T> void update(T entity) {

        try {

            String sheetName =
                    ReflectionUtil.getSheetName(entity.getClass());

            Field idField =
                    ReflectionUtil.getIdField(entity.getClass());

            Object id =
                    idField.get(entity);

            List<Object> row =
                    ReflectionUtil.toRow(entity);

            googleSheetService.updateRow(sheetName, id.toString(), row);

        } catch (Exception e) {

            throw new RuntimeException("Failed to update entity.", e);
        }
    }

    /**
     * Delete Entity
     */
    public <T> void delete(Class<T> clazz,
                           Object id) {

        try {

            String sheetName =
                    ReflectionUtil.getSheetName(clazz);

            googleSheetService.deleteRow(sheetName, id.toString());

        } catch (Exception e) {

            throw new RuntimeException("Failed to delete entity.", e);
        }
    }

}
