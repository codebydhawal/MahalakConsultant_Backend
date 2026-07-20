package com.mahalak.media.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class GoogleSheetsConfig {

    @Value("${google.sheet.application-name}")
    private String applicationName;

    @Value("${google.sheet.credentials}")
    private String credentialsPath;

    @Bean
    public Sheets sheetsService() throws IOException, GeneralSecurityException {

        InputStream inputStream = new ClassPathResource(credentialsPath).getInputStream();

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(inputStream)
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/spreadsheets"));

        HttpRequestInitializer requestInitializer =
                new HttpCredentialsAdapter(credentials);

        NetHttpTransport httpTransport =
                GoogleNetHttpTransport.newTrustedTransport();

        return new Sheets.Builder(
                httpTransport,
                GsonFactory.getDefaultInstance(),
                requestInitializer
        )
                .setApplicationName(applicationName)
                .build();
    }
}
