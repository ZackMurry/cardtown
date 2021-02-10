package com.zackmurry.cardtown;

import com.zackmurry.cardtown.service.UserService;
import com.zackmurry.cardtown.util.JwtUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    private UserDetails userDetails;

    @BeforeAll
    public void createTestUser() {
        final String testEmail = RandomStringUtils.randomAlphanumeric(12);

        //create a new test user if this user already exists
        if (userService.accountExists(testEmail)) {
            createTestUser();
        } else {
            final String testPassword = RandomStringUtils.randomAlphanumeric(12);
            assertDoesNotThrow(() -> userService.createUserAccount(testEmail, "__TEST__", "__USER__", testPassword));
            userDetails = userService.loadUserByUsername(testEmail);
        }

    }

    @AfterAll
    public void deleteTestUser() {
        assertDoesNotThrow(() -> userService.deleteUserAccount(userDetails.getUsername()));
    }

    @DisplayName("Test general jwt creation")
    @Test
    public void testJwtCreation() {
        for (int i = 0; i < 100; i++) {
            final String jwt = jwtUtil.generateToken(userDetails);
            assertTrue(jwt.length() > 20, "Jwt should be sufficiently long.");
            assertTrue(jwtUtil.validateToken(jwt, userDetails), "Jwt should be valid.");
            assertEquals(userDetails.getUsername(), jwtUtil.extractSubject(jwt), "Subject should match email.");
        }
    }

    @DisplayName("Test signing")
    @Test
    public void testJwtSigning() {
        for (int i = 0; i < 100; i++) {
            final String jwt = jwtUtil.generateToken(userDetails);
            assertTrue(jwtUtil.validateToken(jwt, userDetails), "JwtUtil should correctly validate a valid token.");
            for (int j = 0; j < jwt.length(); j++) {
                final char alteredChar = RandomStringUtils.randomAlphanumeric(1).charAt(0);
                final String alteredJwt = jwt.substring(0, j) + (alteredChar != jwt.charAt(j) ? alteredChar : ((char) ((int) alteredChar) + 1)) + jwt.substring(j + 1);
                assertThrows(Exception.class, () -> jwtUtil.validateToken(alteredJwt, userDetails), "JwtUtil should find that an altered JWT is invalid.");
            }
        }
    }

    @DisplayName("Test environment variable for secret key")
    @Test
    public void testEnvironmentVariable() {
        assertNotNull(System.getenv("CARDTOWN_JWT_SECRET_KEY"));
    }

}
