package com.zackmurry.cardtown;

import com.zackmurry.cardtown.exception.ArgumentNotFoundException;
import com.zackmurry.cardtown.exception.BadRequestException;
import com.zackmurry.cardtown.exception.LengthRequiredException;
import com.zackmurry.cardtown.exception.UserNotFoundException;
import com.zackmurry.cardtown.model.arg.ArgumentCreateRequest;
import com.zackmurry.cardtown.model.arg.ResponseArgument;
import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.model.card.CardCreateRequest;
import com.zackmurry.cardtown.model.card.ResponseCard;
import com.zackmurry.cardtown.service.ArgumentService;
import com.zackmurry.cardtown.service.CardService;
import com.zackmurry.cardtown.service.UserService;
import com.zackmurry.cardtown.util.EncryptionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        for (int i = 0; i < 25; i++) {
            final ArgumentCreateRequest req = new ArgumentCreateRequest(RandomStringUtils.randomAlphanumeric(1, 129));
            final String argId = argumentService.createArgument(req);
            assertDoesNotThrow(() -> argumentService.deleteArgument(argId));
        }
    }

    @DisplayName("Test argument name encryption")
    @Test
    public void testArgumentNameEncryption() {
        for (int i = 0; i < 100; i++) {
            final String argumentName = RandomStringUtils.randomAlphanumeric(1, 129);
            final ArgumentCreateRequest req = new ArgumentCreateRequest(argumentName);
            final String argId = argumentService.createArgument(req);
            assertEquals(argumentName, argumentService.getResponseArgumentById(argId).getName());
            assertDoesNotThrow(() -> argumentService.deleteArgument(argId));
        }
    }

    @DisplayName("Test adding and removing cards to/from arguments")
    @Test
    public void testCardAddRemove() {
        for (int i = 0; i < 25; i++) {
            final CardCreateRequest newCard = CardServiceTest.generateMockCard(testEmail);
            final String cardId = cardService.createCard(newCard);
            final String argumentName = RandomStringUtils.randomAlphanumeric(1, 128);
            final String argId =  argumentService.createArgument(new ArgumentCreateRequest(argumentName));
            assertDoesNotThrow(() -> argumentService.addCardToArgument(cardId, argId));
            final ResponseArgument responseArgument = argumentService.getResponseArgumentById(argId);
            assertEquals(argumentName, responseArgument.getName());
            assertEquals(1, responseArgument.getCards().size());
            final ResponseCard returnedCard = responseArgument.getCards().get(0);
            assertEquals(newCard.getTag(), returnedCard.getTag());
            assertEquals(newCard.getCite(), returnedCard.getCite());
            assertEquals(newCard.getCiteInformation(), returnedCard.getCiteInformation());
            assertEquals(newCard.getBodyText(), returnedCard.getBodyText());
            assertEquals(newCard.getBodyHtml(), returnedCard.getBodyHtml());
            assertEquals(newCard.getBodyDraft(), returnedCard.getBodyDraft());
            assertDoesNotThrow(() -> argumentService.removeCardFromArgument(argId, cardId, (short) 0));
            assertEquals(0, argumentService.getResponseArgumentById(argId).getCards().size());
            assertDoesNotThrow(() -> cardService.deleteCardById(cardId));
        }
        for (int i = 0; i < 10; i++) {
            final String argId = argumentService.createArgument(new ArgumentCreateRequest(RandomStringUtils.randomAlphanumeric(1, 128)));
            final String[] cardIds = new String[25];
            for (int j = 0; j < 25; j++) {
                final CardCreateRequest newCard = CardServiceTest.generateMockCard(testEmail);
                final String cardId = cardService.createCard(newCard);
                cardIds[j] = cardId;
                assertDoesNotThrow(() -> argumentService.addCardToArgument(cardId, argId));
            }
            for (short j = 25; j > 0; j--) {
                assertEquals(j, argumentService.getResponseArgumentById(argId).getCards().size());
                argumentService.removeCardFromArgument(argId, cardIds[j - 1], (short) (j - 1));
            }
            assertEquals(0, argumentService.getResponseArgumentById(argId).getCards().size());
            assertDoesNotThrow(() -> argumentService.deleteArgument(argId));
        }
    }

    @DisplayName("Test reordering cards in argument")
    @Test
    public void testReorderCards() {
        // Create argument and add new cards to argument
        final String argumentId = argumentService.createArgument(new ArgumentCreateRequest(RandomStringUtils.randomAlphanumeric(1, 129)));
        final List<String> cardIds = new ArrayList<>(32);
        for (int i = 0; i < 25; i++) {
            final CardCreateRequest newCard = CardServiceTest.generateMockCard(testEmail);
            final String cardId = cardService.createCard(newCard);
            cardIds.add(cardId);
            assertDoesNotThrow(() -> argumentService.addCardToArgument(cardId, argumentId));
        }

        // Test switching one index at a time
        for (int i = 0; i < 100; i++) {
            final int firstIndexToSwitch = RandomUtils.nextInt(0, cardIds.size());
            final int secondIndexToSwitch = RandomUtils.nextInt(0, cardIds.size());
            final String tempId = cardIds.get(firstIndexToSwitch);
            cardIds.set(firstIndexToSwitch, cardIds.get(secondIndexToSwitch));
            cardIds.set(secondIndexToSwitch, tempId);
            argumentService.updateCardPositions(argumentId, cardIds);
            final List<String> updatedPositions = argumentService.getResponseArgumentById(argumentId)
                    .getCards().stream()
                    .map(ResponseCard::getId)
                    .collect(Collectors.toList());
            assertEquals(cardIds.size(), updatedPositions.size());
            for (int j = 0; j < cardIds.size(); j++) {
                assertEquals(cardIds.get(j), updatedPositions.get(j));
            }
        }

        // Test randomizing order
        for (int i = 0; i < 25; i++) {
            Collections.shuffle(cardIds);
            assertDoesNotThrow(() -> argumentService.updateCardPositions(argumentId, cardIds));
            final List<String> updatedPositions = argumentService.getResponseArgumentById(argumentId)
                    .getCards().stream()
                    .map(ResponseCard::getId)
                    .collect(Collectors.toList());
            assertEquals(cardIds.size(), updatedPositions.size());
            for (int j = 0; j < cardIds.size(); j++) {
                assertEquals(cardIds.get(j), updatedPositions.get(j));
            }
        }

        // Clean up
        for (String cardId : cardIds) {
            cardService.deleteCardById(cardId);
        }
        assertDoesNotThrow(() -> argumentService.deleteArgument(argumentId));
    }

}
