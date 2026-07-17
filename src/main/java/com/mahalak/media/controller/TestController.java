package com.mahalak.media.controller;

import com.mahalak.media.config.GoogleOAuthHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class TestController {

    private final GoogleOAuthHelper googleOAuthHelper;

    public TestController(GoogleOAuthHelper googleOAuthHelper) {
        this.googleOAuthHelper = googleOAuthHelper;
    }

    @GetMapping("/google/login")
    public ResponseEntity<Void> login() {

        System.out.println("Google Login Called");

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(googleOAuthHelper.getAuthorizationUrl()))
                .build();
    }

    @GetMapping("/oauth2/callback")
    public String callback(@RequestParam("code") String code) throws Exception {

        googleOAuthHelper.exchangeCode(code);

        return "Google authentication successful.";
    }
}
