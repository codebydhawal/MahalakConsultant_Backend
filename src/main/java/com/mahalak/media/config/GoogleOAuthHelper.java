package com.mahalak.media.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.DriveScopes;
import com.mahalak.media.entity.GoogleOAuthToken;
import com.mahalak.media.repository.GoogleOAuthTokenRepository;
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
    private final GoogleClientSecrets clientSecrets;
    private final String redirectUri;
    private final GoogleOAuthTokenRepository googleOAuthTokenRepository;

    public GoogleOAuthHelper(
            ResourceLoader resourceLoader,
            @Value("${google.drive.credentials}") String credentialsPath,
            @Value("${google.oauth.redirect-uri}") String redirectUri,
            GoogleOAuthTokenRepository googleOAuthTokenRepository) throws Exception {

        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        this.redirectUri = redirectUri;
        this.googleOAuthTokenRepository = googleOAuthTokenRepository;

        InputStream inputStream = openCredentials(resourceLoader, credentialsPath);

        if (inputStream == null) {
            throw new RuntimeException("credentials.json not found");
        }

        this.clientSecrets =
                GoogleClientSecrets.load(
                        GsonFactory.getDefaultInstance(),
                        new InputStreamReader(inputStream)
                );

        flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT,
                GsonFactory.getDefaultInstance(),
                this.clientSecrets,
                Collections.singletonList(DriveScopes.DRIVE)
        )
                .setAccessType("offline")
                .build();
    }

    /**
     * Generates Google OAuth URL
     */
    public String getAuthorizationUrl() {

        GoogleAuthorizationCodeRequestUrl authorizationUrl =
                flow.newAuthorizationUrl();

        authorizationUrl.setRedirectUri(redirectUri);
        // Ensures Google returns a refresh token when the account has already
        // granted this OAuth client permission in the past.
        authorizationUrl.set("prompt", "consent");

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

        String refreshToken = tokenResponse.getRefreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalStateException("Google did not return a refresh token. Please authorize again.");
        }

        googleOAuthTokenRepository.save(GoogleOAuthToken.builder()
                .provider(GoogleOAuthToken.GOOGLE_DRIVE_PROVIDER)
                .refreshToken(refreshToken)
                .build());

        Credential credential = createCredential(refreshToken);
        credential.setAccessToken(tokenResponse.getAccessToken());

        if (tokenResponse.getExpiresInSeconds() != null) {
            credential.setExpirationTimeMilliseconds(
                    System.currentTimeMillis() + tokenResponse.getExpiresInSeconds() * 1000
            );
        }

        return credential;
    }

    /**
     * Returns stored credential
     */
    public Credential getCredential() throws Exception {

        GoogleOAuthToken token = googleOAuthTokenRepository
                .findById(GoogleOAuthToken.GOOGLE_DRIVE_PROVIDER)
                .orElseThrow(() -> new RuntimeException(
                        "Google account not authenticated. Open /google/login once to authorize Google Drive."
                ));

        return createCredential(token.getRefreshToken());
    }

    /**
     * Creates a credential that can exchange the database refresh token for a
     * short-lived Google access token. No token data is written to the filesystem.
     */
    private Credential createCredential(String refreshToken) {
        Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(GsonFactory.getDefaultInstance())
                .setTokenServerUrl(new GenericUrl("https://oauth2.googleapis.com/token"))
                .setClientAuthentication(new ClientParametersAuthentication(
                        clientSecrets.getDetails().getClientId(),
                        clientSecrets.getDetails().getClientSecret()
                ))
                .build();

        credential.setRefreshToken(refreshToken);
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
