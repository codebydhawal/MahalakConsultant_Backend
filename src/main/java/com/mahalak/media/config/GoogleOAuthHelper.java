package com.mahalak.media.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

@Component
public class GoogleOAuthHelper {

    private final NetHttpTransport HTTP_TRANSPORT;
    private final GoogleAuthorizationCodeFlow flow;
    private final String redirectUri;

    public GoogleOAuthHelper(
            ResourceLoader resourceLoader,
            @Value("${google.drive.credentials}") String credentialsPath,
            @Value("${google.oauth.tokens-directory}") String tokensDirectory,
            @Value("${google.oauth.redirect-uri}") String redirectUri) throws Exception {

        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        this.redirectUri = redirectUri;

        InputStream inputStream = openCredentials(resourceLoader, credentialsPath);

        if (inputStream == null) {
            throw new RuntimeException("credentials.json not found");
        }

        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(
                        GsonFactory.getDefaultInstance(),
                        new InputStreamReader(inputStream)
                );

        flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT,
                GsonFactory.getDefaultInstance(),
                clientSecrets,
                Collections.singletonList(DriveScopes.DRIVE)
        )
                .setAccessType("offline")
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokensDirectory)))
                .build();
    }

    /**
     * Generates Google OAuth URL
     */
    public String getAuthorizationUrl() {

        GoogleAuthorizationCodeRequestUrl authorizationUrl =
                flow.newAuthorizationUrl();

        authorizationUrl.setRedirectUri(redirectUri);

        return authorizationUrl.build();
    }

    /**
     * Exchange Authorization Code for Access Token
     */
    public Credential exchangeCode(String code) throws Exception {

        GoogleTokenResponse tokenResponse =
                flow.newTokenRequest(code)
                        .setRedirectUri(redirectUri)
                        .execute();

        return flow.createAndStoreCredential(tokenResponse, "user");
    }

    /**
     * Returns stored credential
     */
    public Credential getCredential() throws Exception {

        Credential credential = flow.loadCredential("user");

        if (credential == null) {
            throw new RuntimeException("Google account not authenticated.");
        }

        return credential;
    }

    private InputStream openCredentials(ResourceLoader resourceLoader, String path) throws Exception {
        Path filePath = Path.of(path);
        if (Files.exists(filePath)) {
            return Files.newInputStream(filePath);
        }

        Resource resource = resourceLoader.getResource("classpath:" + path);
        return resource.getInputStream();
    }
}
