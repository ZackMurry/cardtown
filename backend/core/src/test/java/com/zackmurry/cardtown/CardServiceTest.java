package com.zackmurry.cardtown;

import com.zackmurry.cardtown.exception.UserNotFoundException;
import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.model.card.CardCreateRequest;
import com.zackmurry.cardtown.model.card.CardPreview;
import com.zackmurry.cardtown.model.card.ResponseCard;
import com.zackmurry.cardtown.model.team.TeamCreateRequest;
import com.zackmurry.cardtown.model.team.TeamCreationResponse;
import com.zackmurry.cardtown.model.team.TeamJoinRequest;
import com.zackmurry.cardtown.service.CardService;
import com.zackmurry.cardtown.service.TeamService;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class CardServiceTest {

    @Autowired
    private CardService cardService;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

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

    @DisplayName("Test team encryption")
    @Test
    public void testTeamEncryption() {
        // todo have to add team tests for a lot of things :|

        // Creating a new test user
        String teamOwnerEmail = RandomStringUtils.randomAlphanumeric(12, 40);
        while (userService.accountExists(teamOwnerEmail)) {
            teamOwnerEmail = RandomStringUtils.randomAlphanumeric(12, 40);
        }
        final String teamOwnerPassword = RandomStringUtils.randomAlphanumeric(12, 20);
        final String teamOwnerEmailCopy = teamOwnerEmail; // Create a copy of the email String for lambda
        assertDoesNotThrow(() -> userService.createUserAccount(teamOwnerEmailCopy, "__TEST__", "__USER__", teamOwnerPassword));
        final UserModel teamOwnerModel = userService.getUserModelByEmail(
                teamOwnerEmail,
                encryptionUtils.getSHA256Hash(teamOwnerPassword.getBytes(StandardCharsets.UTF_8))
        ).orElseThrow(RuntimeException::new);
        final var teamOwnerToken = new UsernamePasswordAuthenticationToken(teamOwnerModel, null, teamOwnerModel.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(teamOwnerToken); // Set authentication to team owner user
        // Create team as team owner
        final TeamCreationResponse teamCreationResponse = teamService.createTeam(new TeamCreateRequest(RandomStringUtils.randomAlphanumeric(1, 20)));
        SecurityContextHolder.getContext().setAuthentication(token); // Reset context
        // Join team as test user
        teamService.joinTeam(new TeamJoinRequest(teamCreationResponse.getId(), teamCreationResponse.getSecretKey()));

        for (int i = 0; i < 25; i++) {
            // Create card
            final CardCreateRequest cardCreateRequest = generateMockCard(testEmail);
            final String cardId = cardService.createCard(cardCreateRequest);

            // Make sure that card is properly encrypted/decrypted
            final ResponseCard responseCard = cardService.getResponseCardById(cardId);
            assertEquals(cardCreateRequest.getBodyText(), responseCard.getBodyText());

            // Delete card
            cardService.deleteCardById(cardId);
        }

        // Cleanup created data
        SecurityContextHolder.getContext().setAuthentication(teamOwnerToken);
        teamService.deleteTeam();
        userService.deleteUserAccount(teamOwnerEmail);
        SecurityContextHolder.getContext().setAuthentication(teamOwnerToken);
    }

}
