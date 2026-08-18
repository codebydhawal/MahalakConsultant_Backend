package com.mahalak.media.auth;

import com.mahalak.media.dto.request.LoginRequest;
import com.mahalak.media.entity.User;
import com.mahalak.media.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.auth.InvalidCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    /**
     * Token validity in HOURS.
     */
    private static final long ACCESS_TOKEN_VALIDITY = 5;

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final SecretKey key;
    private final JwtParser jwtParser;
    private final UserRepository userRepository;
    public JwtUtil(UserRepository userRepository, @Value("${app.jwt.secret}") String secretKey) {
        this.userRepository = userRepository;

        this.key = Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );

        this.jwtParser = Jwts.parser()
                .verifyWith(key)
                .build();
    }

    /**
     * Generate JWT using LoginRequest
     */
    public String createToken(LoginRequest request) {

        logger.debug("Generating JWT for {}", request.getEmail());

        Date now = new Date();
        Date expiry = new Date(
                now.getTime() + TimeUnit.HOURS.toMillis(ACCESS_TOKEN_VALIDITY)
        );

        return Jwts.builder()
                .subject(request.getEmail())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    /**
     * Generate JWT using Email
     */
    public String generateToken(String email) throws InvalidCredentialsException {

        logger.debug("Generating JWT for {}", email);

        Date now = new Date();
        Date expiry = new Date(
                now.getTime() + TimeUnit.HOURS.toMillis(ACCESS_TOKEN_VALIDITY)
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException("user not found"));

        return Jwts.builder()
                .subject(user.getEmail())
//                .subject(user.getId().toString())
//                .subject(user.getRole().getRole())
                .claim("id",user.getId())
                .claim("role",user.getRole().getRole())
                .claim("name", user.getFirstName() + " " + user.getLastName())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    /**
     * Parse Claims
     */
    private Claims parseJwtClaims(String token) {

        return jwtParser
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Resolve Claims from Http Request
     */
    public Claims resolveClaims(HttpServletRequest request) {

        try {

            String token = resolveToken(request);

            if (token != null) {
                return parseJwtClaims(token);
            }

            return null;

        } catch (ExpiredJwtException ex) {

            request.setAttribute("expired", ex.getMessage());
            throw ex;

        } catch (Exception ex) {

            request.setAttribute("invalid", ex.getMessage());
            throw ex;
        }
    }

    /**
     * Extract JWT from Authorization Header
     */
    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader(TOKEN_HEADER);

        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }

        return null;
    }

    /**
     * Validate Expiration
     */
    public boolean validateClaims(Claims claims)
            throws AuthenticationException {

        return claims.getExpiration().after(new Date());
    }

    /**
     * Get Email from JWT
     */
    public String getEmail(Claims claims) {
        return claims.getSubject();
    }
}
