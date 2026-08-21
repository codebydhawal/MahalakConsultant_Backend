package com.mahalak.media.framework;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleSheetInitializer {

    private final GoogleEntityScanner entityScanner;
    private final HeaderSynchronizer synchronizer;

    @Value("${google.sheet.initialize}")
    private boolean initializeSheets;

    public void initialize() {

        if (!initializeSheets) {
            System.out.println("Google Sheet initialization is disabled.");
            return;
        }

        entityScanner.scan().forEach(entity -> {

            try {

                synchronizer.synchronize(entity);

                System.out.println(
                        "Google Sheet synchronized : "
                                + entity.getSimpleName());

            } catch (Exception e) {

                System.err.println(
                        "Google Sheet synchronization skipped for "
                                + entity.getSimpleName()
                                + ": "
                                + e.getMessage());
            }
        });
    }
}