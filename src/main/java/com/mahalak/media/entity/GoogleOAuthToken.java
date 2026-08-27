package com.mahalak.media.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Stores the long-lived OAuth credential for the application's Google Drive account.
 * The provider is the primary key because this application uses one shared Drive account.
 */
@Entity
@Table(name = "google_oauth_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleOAuthToken extends BaseEntity {

    public static final String GOOGLE_DRIVE_PROVIDER = "google-drive";

    @Id
    @Column(name = "provider", length = 50, nullable = false, updatable = false)
    private String provider;

    @Column(name = "refresh_token", nullable = false, columnDefinition = "TEXT")
    private String refreshToken;
}
