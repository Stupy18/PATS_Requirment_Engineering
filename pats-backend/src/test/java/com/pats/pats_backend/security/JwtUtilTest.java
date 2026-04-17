package com.pats.pats_backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String SECRET = "pats-super-secret-key-for-tests-must-be-at-least-256-bits-long";
    private static final long EXPIRATION = 86400000L;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();
        Field secretField = JwtUtil.class.getDeclaredField("SECRET_KEY");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, SECRET);

        Field expirationField = JwtUtil.class.getDeclaredField("jwtExpiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtil, EXPIRATION);
    }

    private UserDetails buildUserDetails(String username) {
        return User.withUsername(username)
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    void generateToken_fromUserDetails_returnsNonNullToken() {
        UserDetails userDetails = buildUserDetails("testuser");
        String token = jwtUtil.generateToken(userDetails);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateToken_withClaims_returnsNonNullToken() {
        String token = jwtUtil.generateToken("testuser", "PATIENT", 1L, "test@example.com", 1L);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_returnsCorrectUsername() {
        UserDetails userDetails = buildUserDetails("john");
        String token = jwtUtil.generateToken(userDetails);
        assertEquals("john", jwtUtil.extractUsername(token));
    }

    @Test
    void extractUsername_fromClaimsToken_returnsCorrectUsername() {
        String token = jwtUtil.generateToken("jane", "PSYCHOLOGIST", 2L, "jane@example.com", 2L);
        assertEquals("jane", jwtUtil.extractUsername(token));
    }

    @Test
    void extractExpiration_returnsDateInFuture() {
        String token = jwtUtil.generateToken(buildUserDetails("user1"));
        Date expiration = jwtUtil.extractExpiration(token);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void validateToken_validToken_returnsTrue() {
        UserDetails userDetails = buildUserDetails("testuser");
        String token = jwtUtil.generateToken(userDetails);
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void validateToken_wrongUsername_returnsFalse() {
        String token = jwtUtil.generateToken(buildUserDetails("user1"));
        UserDetails otherUser = buildUserDetails("user2");
        assertFalse(jwtUtil.validateToken(token, otherUser));
    }

    @Test
    void generateToken_differentUsersProduceDifferentTokens() {
        String token1 = jwtUtil.generateToken(buildUserDetails("user1"));
        String token2 = jwtUtil.generateToken(buildUserDetails("user2"));
        assertNotEquals(token1, token2);
    }

    @Test
    void extractClaim_roleFromClaimsToken() {
        String token = jwtUtil.generateToken("testuser", "PATIENT", 1L, "test@example.com", 1L);
        String role = jwtUtil.extractClaim(token, claims -> claims.get("role", String.class));
        assertEquals("PATIENT", role);
    }

    @Test
    void extractClaim_userIdFromClaimsToken() {
        String token = jwtUtil.generateToken("testuser", "PATIENT", 42L, "test@example.com", 10L);
        Long userId = jwtUtil.extractClaim(token, claims -> claims.get("userId", Long.class));
        assertEquals(42L, userId);
    }
}
