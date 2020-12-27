package com.zackmurry.cardtown;

import com.zackmurry.cardtown.dao.user.UserDao;
import com.zackmurry.cardtown.model.auth.AuthenticationResponse;
import com.zackmurry.cardtown.service.UserService;
import com.zackmurry.cardtown.util.EncryptionUtils;
import com.zackmurry.cardtown.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks;

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

    private String testEmail;
    private String testPassword;

    private ResponseEntity<AuthenticationResponse> accountCreationResponse;

    @BeforeAll
    public void createTestUser() {
        testEmail = RandomStringUtils.randomAlphanumeric(12);

        //create a new test user if this user already exists
        if (userService.accountExists(testEmail)) {
            createTestUser();
        } else {
            testPassword = RandomStringUtils.randomAlphanumeric(12);
            accountCreationResponse = userService.createUserAccount(testEmail, "__TEST__", "__USER__", testPassword);
            assertEquals(HttpStatus.OK, accountCreationResponse.getStatusCode());
        }
    }

    @AfterAll
    public void deleteTestUser() {
        assertEquals(HttpStatus.OK, userService.deleteUserAccount(testEmail).getStatusCode());
    }

    @DisplayName("Test if test account exists")
    @Test
    public void testAccountCreation() {
        assertTrue(userService.getUserByEmail(testEmail).isPresent(), "User account should exist.");
    }

    @DisplayName("Test authentication")
    @Test
    public void testAuthentication() {
        assertDoesNotThrow(() -> authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(testEmail, testPassword)), "User should be authenticated if they enter the correct email and password. " + testEmail + "; " + testPassword);
    }

    @DisplayName("Test secret key with account creation")
    @Test
    public void testSecretKey() {
        final byte[] encryptionKey = EncryptionUtils.getEncryptionKey(testPassword);
        assertNotNull(encryptionKey);
        final AuthenticationResponse authRes = accountCreationResponse.getBody();
        assertNotNull(authRes);
        final String jwt = authRes.getJwt();
        assertNotNull(jwt);
        final Claims claims = jwtUtil.extractAllClaims(jwt);
        assertEquals(testEmail, claims.getSubject());
        assertEquals(EncryptionUtils.bytesToHex(encryptionKey), jwtUtil.extractSecretKey(jwt));
        final byte[] allegedSecretKey = userService.getUserSecretKey(testEmail, encryptionKey);
        final String encryptedSecretKey = userDao.getEncryptedSecretKey(testEmail);
        try {
            byte[] actualSecretKey = EncryptionUtils.decryptAES(EncryptionUtils.hexToBytes(encryptedSecretKey), encryptionKey);
            assertEquals(Arrays.toString(actualSecretKey), Arrays.toString(allegedSecretKey));
        } catch (Exception e) {
            fail("When decrypting an encrypted secret key, it should be valid.");
        }
    }

}
