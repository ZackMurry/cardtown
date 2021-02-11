package com.zackmurry.cardtown;

import com.zackmurry.cardtown.exception.ArgumentNotFoundException;
import com.zackmurry.cardtown.exception.BadRequestException;
import com.zackmurry.cardtown.exception.LengthRequiredException;
import com.zackmurry.cardtown.exception.UserNotFoundException;
import com.zackmurry.cardtown.model.arg.ArgumentCreateRequest;
import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.service.ArgumentService;
import com.zackmurry.cardtown.service.CardService;
import com.zackmurry.cardtown.service.UserService;
import com.zackmurry.cardtown.util.EncryptionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class ArgumentServiceTest {

    @Autowired
    private ArgumentService argumentService;

    @Autowired
    private CardService cardService;

    @Autowired
    private UserService userService;

    @Autowired
    private EncryptionUtils encryptionUtils;

    private String testEmail;
    private String testPassword;

    private UsernamePasswordAuthenticationToken token;

    @BeforeAll
    public void createTestUser() throws UserNotFoundException {
        testEmail = RandomStringUtils.randomAlphanumeric(12);

        // try again if this user already exists
        if (userService.accountExists(testEmail)) {
            createTestUser();
        } else {
            testPassword = RandomStringUtils.randomAlphanumeric(12);
            assertDoesNotThrow(() -> userService.createUserAccount(testEmail, "__TEST__", "__USER__", testPassword));
            User user = userService.getUserByEmail(testEmail).orElse(null);
            assertNotNull(user, "User should be found in database");
            byte[] secretKey = userService.getUserSecretKey(testEmail, encryptionUtils.getSHA256Hash(testPassword.getBytes(StandardCharsets.UTF_8)));
            UserModel userModel = new UserModel(user, secretKey);
            token = new UsernamePasswordAuthenticationToken(userModel, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(token);
        }
    }

    @AfterAll
    public void deleteTestUser() {
        assertDoesNotThrow(() -> userService.deleteUserAccount(testEmail));
    }

    @BeforeEach
    public void setAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @DisplayName("Test argument creation and deletion")
    @Test
    public void testCreateAndDelete() {
        for (int i = 0; i < 25; i++) {
            final ArgumentCreateRequest req = new ArgumentCreateRequest(RandomStringUtils.randomAlphanumeric(25));
            final String argId = argumentService.createArgument(req);
            assertDoesNotThrow(() -> argumentService.getResponseArgumentById(argId));
            assertDoesNotThrow(() -> argumentService.deleteArgument(argId));
            assertThrows(ArgumentNotFoundException.class, () -> argumentService.getResponseArgumentById(argId));
        }
    }

    @DisplayName("Test argument name restrictions")
    @Test
    public void testArgumentNameRestrictions() {
        for (int i = 0; i < 25; i++) {
            final ArgumentCreateRequest req = new ArgumentCreateRequest(RandomStringUtils.randomAlphanumeric(129, 256));
            assertThrows(LengthRequiredException.class, () -> argumentService.createArgument(req));
        }
        for (int i = 0; i < 25; i++) {
            final ArgumentCreateRequest req = new ArgumentCreateRequest();
            assertThrows(BadRequestException.class, () -> argumentService.createArgument(req));
        }
    }

    @DisplayName("Test argument name encryption")
    @Test
    public void testArgumentNameEncryption() {
        for (int i = 0; i < 100; i++) {
            final String argumentName = RandomStringUtils.randomAlphanumeric(0, 129);
            final ArgumentCreateRequest req = new ArgumentCreateRequest(argumentName);
            final String argId = argumentService.createArgument(req);
            assertEquals(argumentName, argumentService.getResponseArgumentById(argId).getName());
            assertDoesNotThrow(() -> argumentService.deleteArgument(argId));
        }
    }

}
