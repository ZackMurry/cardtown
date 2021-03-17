package com.zackmurry.cardtown;

import com.zackmurry.cardtown.exception.UserNotFoundException;
import com.zackmurry.cardtown.model.action.ActionEntity;
import com.zackmurry.cardtown.model.action.ActionType;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.service.*;
import com.zackmurry.cardtown.util.EncryptionUtils;
import com.zackmurry.cardtown.util.UUIDCompressor;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class ActionServiceTest {

    @Autowired
    private ArgumentService argumentService;

    @Autowired
    private CardService cardService;

    @Autowired
    private UserService userService;

    @Autowired
    private EncryptionUtils encryptionUtils;

    @Autowired
    private ActionService actionService;

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
            final UserModel userModel = userService.getUserModelByEmail(
                    testEmail,
                    encryptionUtils.getSHA256Hash(testPassword.getBytes(StandardCharsets.UTF_8))
            ).orElseThrow(UserNotFoundException::new);
            token = new UsernamePasswordAuthenticationToken(userModel, null, userModel.getAuthorities());
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

    @DisplayName("Test adding/viewing actions")
    @Test
    public void testRegisterActions() {
        final String cardId = cardService.createCard(CardServiceTest.generateMockCard(testEmail));
        List<ActionEntity> actions = actionService.getAllActionsVisibleToUser();
        assertEquals(1, actions.size());
        final ActionEntity cardCreateAction = actions.get(0);
        assertEquals(ActionType.CREATE_CARD, cardCreateAction.getActionType());
        assertEquals(((UserModel) token.getPrincipal()).getId(), cardCreateAction.getSubjectId());
        assertEquals(UUIDCompressor.decompress(cardId), cardCreateAction.getCardId());
        cardService.deleteCardById(cardId);
        actions = actionService.getAllActionsVisibleToUser();
        assertEquals(2, actions.size());
        final ActionEntity cardDeleteAction = actions.get(0);
        assertEquals(ActionType.DELETE_CARD, cardDeleteAction.getActionType());
        assertEquals(((UserModel) token.getPrincipal()).getId(), cardDeleteAction.getSubjectId());
        assertEquals(UUIDCompressor.decompress(cardId), cardDeleteAction.getCardId());
    }

    // todo add more tests

}
