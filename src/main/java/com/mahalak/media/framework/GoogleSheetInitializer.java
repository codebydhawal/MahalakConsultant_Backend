package com.mahalak.media.framework;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleSheetInitializer {

    private final GoogleEntityScanner entityScanner;

    private final HeaderSynchronizer synchronizer;

    @PostConstruct
    public void initialize() {

        entityScanner.scan().forEach(entity -> {

            try {

                synchronizer.synchronize(entity);

                System.out.println(
                        "Google Sheet synchronized : "
                                + entity.getSimpleName());

            } catch (Exception e) {
                // A temporary Google Sheets quota error must not stop the web application.
                // The entity manager will retry synchronization on the first write.
                System.err.println("Google Sheet synchronization skipped for "
                        + entity.getSimpleName() + ": " + e.getMessage());
            }

        });

    }

}
