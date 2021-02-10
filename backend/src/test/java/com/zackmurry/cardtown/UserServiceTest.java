package com.zackmurry.cardtown;

import com.zackmurry.cardtown.dao.user.UserDao;
import com.zackmurry.cardtown.exception.UserNotFoundException;
import com.zackmurry.cardtown.model.auth.AuthenticationRequest;
import com.zackmurry.cardtown.model.auth.AuthenticationResponse;
import com.zackmurry.cardtown.service.UserService;
import com.zackmurry.cardtown.util.EncryptionUtils;
import com.zackmurry.cardtown.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EncryptionUtils encryptionUtils;

    private String testEmail;
    private String testPassword;

    @BeforeAll
    public void createTestUser() {
        testEmail = RandomStringUtils.randomAlphanumeric(12);

        // try again if this user already exists
        if (userService.accountExists(testEmail)) {
            createTestUser();
        } else {
            testPassword = RandomStringUtils.randomAlphanumeric(12);
            assertDoesNotThrow(() -> userService.createUserAccount(testEmail, "__TEST__", "__USER__", testPassword));
        }
    }

    @AfterAll
    public void deleteTestUser() {
        assertDoesNotThrow(() -> userService.deleteUserAccount(testEmail));
    }

    @DisplayName("Test if test account exists")
    @Test
    public void testAccountCreation() {
        assertTrue(userService.getUserByEmail(testEmail).isPresent(), "User account should exist.");
    }

    @DisplayName("Test authentication")
    @Test
    public void testAuthentication() {
        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(testEmail, testPassword)), "User should be authenticated if they enter the correct email and password. " + testEmail + "; " + testPassword);
        }
    }

    @DisplayName("Test secret key with account creation")
    @Test
    public void testSecretKey() {
        final byte[] encryptionKey = encryptionUtils.getSHA256Hash(testPassword.getBytes(StandardCharsets.UTF_8));
        assertNotNull(encryptionKey);
        final AuthenticationResponse authRes = userService.createAuthenticationToken(testEmail, testPassword);
        assertNotNull(authRes);
        final String jwt = authRes.getJwt();
        assertNotNull(jwt);
        final Claims claims = jwtUtil.extractAllClaims(jwt);
        assertEquals(testEmail, claims.getSubject());
        assertEquals(Base64.encodeBase64String(encryptionKey), jwtUtil.extractEncryptionKey(jwt));
        byte[] allegedSecretKey;
        try {
            allegedSecretKey = userService.getUserSecretKey(testEmail, encryptionKey);
        } catch (UserNotFoundException ignored) {
            fail("A secret key should be able to be retrieved");
            return;
        }
        final String encryptedSecretKey = userDao.getEncryptedSecretKey(testEmail).orElse(null);
        assertNotNull(encryptedSecretKey, "An encrypted secret key should not be null");
        try {
            byte[] actualSecretKey = EncryptionUtils.decryptAES(Base64.decodeBase64(encryptedSecretKey), encryptionKey);
            assertEquals(Arrays.toString(actualSecretKey), Arrays.toString(allegedSecretKey));
        } catch (Exception e) {
            fail("When decrypting an encrypted secret key, it should be valid.");
        }
    }

}
