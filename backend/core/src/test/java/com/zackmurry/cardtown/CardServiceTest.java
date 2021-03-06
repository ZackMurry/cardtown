package com.zackmurry.cardtown;

import com.zackmurry.cardtown.exception.UserNotFoundException;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.model.card.CardCreateRequest;
import com.zackmurry.cardtown.model.card.CardEntity;
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
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    static boolean createRequestEqualsResponse(@NonNull CardCreateRequest request, @NonNull ResponseCard response) {
        return request.getTag().equals(response.getTag()) &&
                request.getCite().equals(response.getCite()) &&
                request.getCiteInformation().equals(response.getCiteInformation()) &&
                request.getBodyText().equals(response.getBodyText()) &&
                request.getBodyDraft().equals(response.getBodyDraft()) &&
                request.getBodyHtml().equals(response.getBodyHtml());
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
            assertTrue(createRequestEqualsResponse(req, returnedCard));
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
            assertTrue(createRequestEqualsResponse(updateReq, updatedCard));
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

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Test team integration")
    class TestWithTeams {

        private String teamOwnerEmail;
        private String teamOwnerPassword;
        private UsernamePasswordAuthenticationToken teamOwnerToken;

        @BeforeAll
        public void initializeTeam() {
            // Creating a new test user
            teamOwnerEmail = RandomStringUtils.randomAlphanumeric(12, 40);
            while (userService.accountExists(teamOwnerEmail)) {
                teamOwnerEmail = RandomStringUtils.randomAlphanumeric(12, 40);
            }
            teamOwnerPassword = RandomStringUtils.randomAlphanumeric(12, 20);
            final String teamOwnerEmailCopy = teamOwnerEmail; // Create a copy of the email String for lambda
            assertDoesNotThrow(() -> userService.createUserAccount(teamOwnerEmailCopy, "__TEST__", "__USER__", teamOwnerPassword));
            UserModel teamOwnerModel = userService.getUserModelByEmail(
                    teamOwnerEmail,
                    encryptionUtils.getSHA256Hash(teamOwnerPassword.getBytes(StandardCharsets.UTF_8))
            ).orElseThrow(RuntimeException::new);
            teamOwnerToken = new UsernamePasswordAuthenticationToken(teamOwnerModel, null, teamOwnerModel.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(teamOwnerToken); // Set authentication to team owner user
            // Create team as team owner
            final TeamCreationResponse teamCreationResponse = teamService.createTeam(new TeamCreateRequest(RandomStringUtils.randomAlphanumeric(1, 20)));
            teamOwnerToken = userService.regenerateTokenDetails(teamOwnerToken, teamOwnerPassword);
            SecurityContextHolder.getContext().setAuthentication(token); // Reset context
            // Join team as test user
            teamService.joinTeam(new TeamJoinRequest(teamCreationResponse.getId(), teamCreationResponse.getSecretKey()));
            token = userService.regenerateTokenDetails(token, testPassword);
            SecurityContextHolder.getContext().setAuthentication(token);
        }

        @AfterAll
        public void cleanUpTeam() {
            SecurityContextHolder.getContext().setAuthentication(teamOwnerToken);
            teamService.deleteTeam();
            userService.deleteUserAccount(teamOwnerEmail);
            SecurityContextHolder.getContext().setAuthentication(userService.regenerateTokenDetails(token, testPassword));
        }

        @AfterEach
        public void resetContext() {
            SecurityContextHolder.getContext().setAuthentication(userService.regenerateTokenDetails(token, testPassword));
        }

        @DisplayName("Test team encryption")
        @Test
        public void testTeamEncryption() {
            // todo have to add team tests for a lot of things :|
            for (int i = 0; i < 25; i++) {
                // Create card as testUser
                final CardCreateRequest cardCreateRequest = generateMockCard(testEmail);
                final String cardId = cardService.createCard(cardCreateRequest);

                // Make sure that card is properly encrypted/decrypted
                final ResponseCard responseCard = cardService.getResponseCardById(cardId);
                assertTrue(createRequestEqualsResponse(cardCreateRequest, responseCard));

                // Switch context to team owner and check access to card
                SecurityContextHolder.getContext().setAuthentication(teamOwnerToken);
                final ResponseCard teamOwnerResponseCard = cardService.getResponseCardById(cardId);
                assertTrue(createRequestEqualsResponse(cardCreateRequest, teamOwnerResponseCard));

                // Delete card
                SecurityContextHolder.getContext().setAuthentication(token);
                cardService.deleteCardById(cardId);
            }
        }

        @DisplayName("Test team editing")
        @Test
        public void testTeamEditing() {
            for (int i = 0; i < 25; i++) {
                // Create card as testUser
                final CardCreateRequest cardCreateRequest = generateMockCard(testEmail);
                final String cardId = cardService.createCard(cardCreateRequest);

                // Edit card as teamOwner
                SecurityContextHolder.getContext().setAuthentication(teamOwnerToken);
                final CardCreateRequest editRequest = generateMockCard(testEmail);
                assertDoesNotThrow(() -> cardService.updateCardById(cardId, editRequest), "Team members should be able to edit cards");

                // Check that testUser sees the update
                SecurityContextHolder.getContext().setAuthentication(token);
                final ResponseCard responseCard = cardService.getResponseCardById(cardId);
                assertTrue(createRequestEqualsResponse(editRequest, responseCard));

                // Delete the card
                cardService.deleteCardById(cardId);
            }
        }

        @DisplayName("Test team deleting")
        @Test
        public void testTeamDeleting() {
            for (int i = 0; i < 25; i++) {
                // Create card as testUser
                final CardCreateRequest cardCreateRequest = generateMockCard(testEmail);
                final String cardId = cardService.createCard(cardCreateRequest);

                // Delete card as teamOwner
                SecurityContextHolder.getContext().setAuthentication(teamOwnerToken);
                assertDoesNotThrow(() -> cardService.deleteCardById(cardId), "Team members should be able to delete cards");
                // Switch context back
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }

        @DisplayName("Test get all cards in team")
        @Test
        public void testTeamGetAllCards() {
            // Create 25 cards as testUser
            // Map of a card id to the CardCreateRequest for it
            final Map<String, CardCreateRequest> cardCreateRequestsMap = new HashMap<>();
            for (int i = 0; i < 25; i++) {
                final CardCreateRequest cardCreateRequest = generateMockCard(testEmail);
                cardCreateRequestsMap.put(cardService.createCard(cardCreateRequest), cardCreateRequest);
            }

            // Switch context to teamOwner and see if they are still present
            SecurityContextHolder.getContext().setAuthentication(teamOwnerToken);
            final List<ResponseCard> responseCards = cardService.getAllCardsVisibleToUser();
            for (ResponseCard rc : responseCards) {
                assertTrue(cardCreateRequestsMap.containsKey(rc.getId()));
                final CardCreateRequest createRequest = cardCreateRequestsMap.remove(rc.getId());
                assertTrue(createRequestEqualsResponse(createRequest, rc));
                cardService.deleteCardById(rc.getId()); // Delete the card now that we're done
            }
            assertTrue(cardCreateRequestsMap.isEmpty());
        }

    }

}
