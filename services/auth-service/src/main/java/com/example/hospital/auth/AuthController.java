package com.example.hospital.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@RestController @RequestMapping("/auth")
public class AuthController {
    private final String clientId;
    private final SecretKey signingKey;
    private final long ttlMinutes;
    public AuthController(@Value("${google.client-id}") String clientId,
                          @Value("${app.jwt.secret}") String secret,
                          @Value("${app.jwt.ttl-minutes}") long ttlMinutes) {
        if (secret.length() < 32) throw new IllegalArgumentException("JWT secret must contain at least 32 characters");
        this.clientId = clientId; this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.ttlMinutes = ttlMinutes;
    }
    @PostMapping("/google")
    public ResponseEntity<?> google(@RequestBody GoogleTokenRequest request) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId)).build();
        GoogleIdToken token = verifier.verify(request.idToken());
        if (token == null || token.getPayload().getEmail() == null || !Boolean.TRUE.equals(token.getPayload().getEmailVerified())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid Google ID token"));
        }
        String email = token.getPayload().getEmail();
        Instant now = Instant.now();
        String jwt = Jwts.builder().subject(email).claim("email", email).claim("provider", "google")
                .issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(ttlMinutes * 60)))
                .signWith(signingKey).compact();
        return ResponseEntity.ok(Map.of("token", jwt, "email", email, "expiresInSeconds", ttlMinutes * 60));
    }
    public record GoogleTokenRequest(@NotBlank String idToken) {}
}
