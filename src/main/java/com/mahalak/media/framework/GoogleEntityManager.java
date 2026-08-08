package com.mahalak.media.framework;

import com.mahalak.media.IServices.IGoogleSheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class GoogleEntityManager {

    private final IGoogleSheetService googleSheetService;

    private final HeaderSynchronizer headerSynchronizer;

    private final GoogleIdGenerator googleIdGenerator;

    private final Map<String, CacheEntry<List<?>>> cache =
            new ConcurrentHashMap<>();

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
            cache.remove(sheetName);

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

            // Get cache TTL
            long ttl = getCacheTTL(clazz);

            /*
             * ============================================================
             * CACHE ENABLED
             * ============================================================
             */
            if (ttl > 0) {

                CacheEntry<List<?>> cacheEntry =
                        cache.get(sheetName);

                if (cacheEntry != null) {

                    long currentTime =
                            System.currentTimeMillis();

                    // Cache still valid
                    if (currentTime - cacheEntry.getTimestamp() < ttl) {

                        return (List<T>) cacheEntry.getData();
                    }

                    // Cache expired
                    cache.remove(sheetName);
                }
            }

            /*
             * ============================================================
             * CACHE MISS / CACHE DISABLED
             * ============================================================
             */

            List<List<Object>> rows =
                    googleSheetService.getAllRows(sheetName);

            List<T> entities = rows.stream()
                    .skip(1)
                    .map(row -> ReflectionUtil.toEntity(clazz, row))
                    .toList();

            /*
             * ============================================================
             * STORE IN CACHE ONLY IF CACHE IS ENABLED
             * ============================================================
             */

            if (ttl > 0) {

                cache.put(
                        sheetName,
                        new CacheEntry<>(
                                entities,
                                System.currentTimeMillis()
                        )
                );
            }

            return entities;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to fetch entities.",
                    e
            );
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

            idField.setAccessible(true);

            return findAll(clazz)
                    .stream()
                    .filter(entity -> {

                        try {

                            Object value = idField.get(entity);

                            return value != null &&
                                    value.equals(id);

                        } catch (Exception e) {

                            return false;
                        }

                    })
                    .findFirst();

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
            cache.remove(sheetName);
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
            cache.remove(sheetName);
        } catch (Exception e) {

            throw new RuntimeException("Failed to delete entity.", e);
        }
    }

    private boolean shouldCache(Class<?> clazz) {

        return clazz.isAnnotationPresent(CacheableEntity.class);

    }

    private long getCacheTTL(Class<?> entityClass) {

        CacheableEntity annotation =
                entityClass.getAnnotation(CacheableEntity.class);

        if (annotation == null) {
            return 0;
        }

        return annotation.ttl();
    }
}
