package com.mahalak.media.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleDriveConfig {

    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Bean
    public Drive.Builder driveBuilder() throws Exception {

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                null
        );
    }
}