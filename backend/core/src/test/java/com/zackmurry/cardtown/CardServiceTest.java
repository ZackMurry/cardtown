package com.zackmurry.cardtown;

import com.zackmurry.cardtown.exception.UserNotFoundException;
import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.model.card.CardCreateRequest;
import com.zackmurry.cardtown.model.card.CardPreview;
import com.zackmurry.cardtown.model.card.ResponseCard;
import com.zackmurry.cardtown.service.CardService;
import com.zackmurry.cardtown.service.UserService;
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
public class CardServiceTest {

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

    static CardCreateRequest generateMockCard(String email) {
        return new CardCreateRequest(
                email, // owner email
                RandomStringUtils.randomAlphanumeric(25), // tag
                RandomStringUtils.randomAlphanumeric(25), // cite
                RandomStringUtils.randomAlphanumeric(25), // cite information
                "<p>" + RandomStringUtils.randomAlphanumeric(50) + "</p>", // body html
                "{ \"sampleContent\": \"" + RandomStringUtils.randomAlphanumeric(100) + "\" }", // body draft
                RandomStringUtils.randomAlphanumeric(100) // body text
        );

    }

    @DisplayName("Test creating and deleting cards")
    @Test
    public void testCreateAndDeleteCard() {
        for (int i = 0; i < 100; i++) {
            final String cardId = cardService.createCard(generateMockCard(testEmail));
            assertDoesNotThrow(() -> cardService.deleteCardById(cardId));
            assertTrue(cardService.getCardEntityById(UUIDCompressor.decompress(cardId)).isEmpty());
        }
    }

    @DisplayName("Test card encryption")
    @Test
    public void testCardEncryption() {
        for (int i = 0; i < 100; i++) {
            final CardCreateRequest req = generateMockCard(testEmail);
            final String cardId = cardService.createCard(req);
            final ResponseCard returnedCard = cardService.getResponseCardById(cardId);
            assertEquals(req.getTag(), returnedCard.getTag(), "The tag should not change after being stored in the database");
            assertEquals(req.getCite(), returnedCard.getCite(), "The cite should not change after being stored in the database");
            assertEquals(req.getCiteInformation(), returnedCard.getCiteInformation(), "The cite should not change after being stored in the database");
            assertEquals(req.getBodyHtml(), returnedCard.getBodyHtml(), "The body html should not change after being stored in the database");
            assertEquals(req.getBodyDraft(), returnedCard.getBodyDraft(), "The body draft should not change after being stored in the database");
            assertEquals(req.getBodyText(), returnedCard.getBodyText(), "The body text should not change after being stored in the database");
            assertDoesNotThrow(() -> cardService.deleteCardById(cardId));
            assertTrue(cardService.getCardEntityById(UUIDCompressor.decompress(cardId)).isEmpty());
        }
    }

    @DisplayName("Test editing cards")
    @Test
    public void testEditCard() {
        final CardCreateRequest createReq = generateMockCard(testEmail);
        final String cardId = cardService.createCard(createReq);
        for (int i = 0; i < 100; i++) {
            final CardCreateRequest updateReq = generateMockCard(testEmail);
            assertDoesNotThrow(() -> cardService.updateCardById(cardId, updateReq));
            final ResponseCard updatedCard = cardService.getResponseCardById(cardId);
            assertEquals(updateReq.getTag(), updatedCard.getTag());
            assertEquals(updateReq.getCite(), updatedCard.getCite());
            assertEquals(updateReq.getCiteInformation(), updatedCard.getCiteInformation());
            assertEquals(updateReq.getBodyHtml(), updatedCard.getBodyHtml());
            assertEquals(updateReq.getBodyDraft(), updatedCard.getBodyDraft());
            assertEquals(updateReq.getBodyText(), updatedCard.getBodyText());
        }
        assertDoesNotThrow(() -> cardService.deleteCardById(cardId));
        assertTrue(cardService.getCardEntityById(UUIDCompressor.decompress(cardId)).isEmpty());
    }

    @DisplayName("Test card previews")
    @Test
    public void testCardPreviews() {
        final CardCreateRequest[] cardCreateRequests = new CardCreateRequest[10];
        final String[] cardIds = new String[cardCreateRequests.length];
        for (int i = 0; i < cardCreateRequests.length; i++) {
            final CardCreateRequest createRequest = generateMockCard(testEmail);
            cardCreateRequests[i] = createRequest;
            cardIds[i] = cardService.createCard(createRequest);
        }
        final List<CardPreview> cardPreviews = cardService.getCardPreviewsByUser();
        assertEquals(cardCreateRequests.length, cardPreviews.size());
        for (int i = 0; i < cardCreateRequests.length; i++) {
            // For some reason, Java gives an error saying that `i` is not final or effectively final
            // So, I'll just declare a final variable equal to the current value of `i`
            final int index = i;
            final CardPreview cardPreview = cardPreviews.stream().filter(cp -> cp.getId().equals(cardIds[index])).findFirst().orElse(null);
            assertNotNull(cardPreview, "A created card should be found when all card previews are requested");
            final CardCreateRequest cardCreateRequest = cardCreateRequests[i];
            assertEquals(cardCreateRequest.getTag(), cardPreview.getTag());
            assertEquals(cardCreateRequest.getCite(), cardPreview.getCite());
            assertEquals(cardCreateRequest.getBodyText(), cardPreview.getBodyText());
        }
        for (String cardId : cardIds) {
            assertDoesNotThrow(() -> cardService.deleteCardById(cardId));
            assertTrue(cardService.getCardEntityById(UUIDCompressor.decompress(cardId)).isEmpty());
        }
    }

}
