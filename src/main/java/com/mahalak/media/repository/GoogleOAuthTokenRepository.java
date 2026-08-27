package com.mahalak.media.repository;

import com.mahalak.media.entity.GoogleOAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoogleOAuthTokenRepository extends JpaRepository<GoogleOAuthToken, String> {
}
