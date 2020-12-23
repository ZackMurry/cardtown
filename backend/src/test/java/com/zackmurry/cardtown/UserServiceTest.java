package com.zackmurry.cardtown;

import com.zackmurry.cardtown.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String testEmail;
    private String testPassword;

    @BeforeAll
    public void createTestUser() {
        testEmail = RandomStringUtils.randomAlphanumeric(12);

        //create a new test user if this user already exists
        if (userService.accountExists(testEmail)) {
            createTestUser();
        } else {
            testPassword = RandomStringUtils.randomAlphanumeric(12);
            assertEquals(HttpStatus.OK, userService.createUserAccount(testEmail, "__TEST__", "__USER__", testPassword).getStatusCode());
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

}
