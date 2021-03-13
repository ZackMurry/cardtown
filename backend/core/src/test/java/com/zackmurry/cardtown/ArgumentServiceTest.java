package com.zackmurry.cardtown;

import com.zackmurry.cardtown.dao.arg.ArgumentDao;
import com.zackmurry.cardtown.exception.ArgumentNotFoundException;
import com.zackmurry.cardtown.exception.BadRequestException;
import com.zackmurry.cardtown.exception.LengthRequiredException;
import com.zackmurry.cardtown.exception.UserNotFoundException;
import com.zackmurry.cardtown.model.arg.ArgumentCreateRequest;
import com.zackmurry.cardtown.model.arg.ResponseArgument;
import com.zackmurry.cardtown.model.arg.card.ArgumentWithCardModel;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.model.card.CardCreateRequest;
import com.zackmurry.cardtown.model.card.ResponseCard;
import com.zackmurry.cardtown.model.team.TeamCreateRequest;
import com.zackmurry.cardtown.model.team.TeamCreationResponse;
import com.zackmurry.cardtown.model.team.TeamJoinRequest;
import com.zackmurry.cardtown.service.ArgumentService;
import com.zackmurry.cardtown.service.CardService;
import com.zackmurry.cardtown.service.TeamService;
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
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

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

    @Autowired
    private TeamService teamService;

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
            assertDoesNotThrow(() -> argumentService.addCardToArgument(argId, cardId));
            final ResponseArgument responseArgument = argumentService.getResponseArgumentById(argId);
            assertEquals(argumentName, responseArgument.getName());
            assertEquals(1, responseArgument.getCards().size());
            final ResponseCard returnedCard = responseArgument.getCards().get(0);
            assertTrue(CardServiceTest.createRequestEqualsResponse(newCard, returnedCard));
            assertDoesNotThrow(() -> argumentService.removeCardFromArgument(argId, cardId, (short) 0));
            assertEquals(0, argumentService.getResponseArgumentById(argId).getCards().size());
            cardService.deleteCardById(cardId);
            argumentService.deleteArgument(argId);
        }
        for (int i = 0; i < 10; i++) {
            final String argId = argumentService.createArgument(new ArgumentCreateRequest(RandomStringUtils.randomAlphanumeric(1, 128)));
            final String[] cardIds = new String[25];
            for (int j = 0; j < 25; j++) {
                final CardCreateRequest newCard = CardServiceTest.generateMockCard(testEmail);
                final String cardId = cardService.createCard(newCard);
                cardIds[j] = cardId;
                assertDoesNotThrow(() -> argumentService.addCardToArgument(argId, cardId));
            }
            for (short j = 25; j > 0; j--) {
                assertEquals(j, argumentService.getResponseArgumentById(argId).getCards().size());
                argumentService.removeCardFromArgument(argId, cardIds[j - 1], (short) (j - 1));
                cardService.deleteCardById(cardIds[j - 1]);
            }
            assertEquals(0, argumentService.getResponseArgumentById(argId).getCards().size());
            argumentService.deleteArgument(argId);
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
            assertDoesNotThrow(() -> argumentService.addCardToArgument(argumentId, cardId));
        }

        // Test switching one index at a time
        for (int i = 0; i < 100; i++) {
            final short oldIndex = (short) RandomUtils.nextInt(0, cardIds.size());
            final short newIndex = (short) RandomUtils.nextInt(0, cardIds.size());
            final String tempId = cardIds.get(oldIndex);
            cardIds.remove(oldIndex);
            cardIds.add(newIndex, tempId);
            argumentService.updateCardPositions(argumentId, newIndex, oldIndex);
            final List<String> updatedPositions = argumentService.getResponseArgumentById(argumentId)
                    .getCards().stream()
                    .map(ResponseCard::getId)
                    .collect(Collectors.toList());
            assertEquals(cardIds.size(), updatedPositions.size());
            for (int j = 0; j < cardIds.size(); j++) {
                assertEquals(cardIds.get(j), updatedPositions.get(j));
                // todo test indices in argument
            }
        }

        // Clean up
        for (String cardId : cardIds) {
            cardService.deleteCardById(cardId);
        }
        assertDoesNotThrow(() -> argumentService.deleteArgument(argumentId));
    }

    @DisplayName("Test renaming arguments")
    @Test
    public void testArgumentRename() {
        for (int i = 0; i < 10; i++) {
            final String argumentName = RandomStringUtils.randomAlphanumeric(25);
            final String argId = argumentService.createArgument(new ArgumentCreateRequest(argumentName));
            for (int j = 0; j < 25; j++) {
                final String newArgumentName = RandomStringUtils.randomAlphanumeric(25);
                assertDoesNotThrow(() -> argumentService.renameArgument(argId, newArgumentName));
                final ResponseArgument responseArgument = argumentService.getResponseArgumentById(argId);
                assertEquals(newArgumentName, responseArgument.getName());
            }
            assertDoesNotThrow(() -> argumentService.deleteArgument(argId));
        }
    }

    @DisplayName("Test getting argument previews by card")
    @Test
    public void testGetArgumentPreviewsByCard() {
        for (int i = 0; i < 10; i++) {
            final CardCreateRequest cardCreateRequest = CardServiceTest.generateMockCard(testEmail);
            final String cardId = cardService.createCard(cardCreateRequest);
            final int numNewArgumentsToMake = RandomUtils.nextInt(0, 10);
            final List<String> argumentIds = new ArrayList<>();
            for (int j = 0; j < numNewArgumentsToMake; j++) {
                final String argumentId = argumentService.createArgument(new ArgumentCreateRequest(RandomStringUtils.randomAlphanumeric(25)));
                argumentIds.add(argumentId);
                argumentService.addCardToArgument(argumentId, cardId);
            }
            final List<ArgumentWithCardModel> argumentPreviews = argumentService.getArgumentPreviewsByCardId(cardId);
            assertEquals(numNewArgumentsToMake, argumentPreviews.size());
            for (ArgumentWithCardModel argumentWithCardModel : argumentPreviews) {
                assertTrue(argumentIds.contains(argumentWithCardModel.getId()));
                argumentService.deleteArgument(argumentWithCardModel.getId());
                argumentIds.remove(argumentWithCardModel.getId());
            }
            assertEquals(0, argumentIds.size());
            cardService.deleteCardById(cardId);
        }
    }

    @DisplayName("Test getting number of arguments by user")
    @Test
    public void testNumberOfArguments() {
        // Test preconditions
        assumeTrue(argumentService.getNumberOfArgumentsByUser() == 0);

        for (int i = 0; i < 10; i++) {
            final List<String> argIds = new ArrayList<>();
            for (int j = 1; j <= 25; j++) {
                final ArgumentCreateRequest argumentCreateRequest = new ArgumentCreateRequest(RandomStringUtils.randomAlphanumeric(25));
                argIds.add(argumentService.createArgument(argumentCreateRequest));
                assertEquals(j, argumentService.getNumberOfArgumentsByUser());
            }
            for (String argId : argIds) {
                argumentService.deleteArgument(argId);
            }
            assertEquals(0, argumentService.getNumberOfArgumentsByUser());
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
            final UserModel teamOwnerModel = userService.getUserModelByEmail(
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
            for (int i = 0; i < 25; i++) {
                // Create argument as testUser
                final String argumentName = RandomStringUtils.randomAlphanumeric(25);
                final ArgumentCreateRequest argumentCreateRequest = new ArgumentCreateRequest(argumentName);
                final String argId = argumentService.createArgument(argumentCreateRequest);

                // Check access to and encryption of argument as creator
                final ResponseArgument responseArgument = argumentService.getResponseArgumentById(argId);
                assertEquals(responseArgument.getName(), argumentName);

                // Check access to and encryption of argument as team member
                SecurityContextHolder.getContext().setAuthentication(teamOwnerToken);
                final ResponseArgument teamOwnerResponseArgument = argumentService.getResponseArgumentById(argId);
                assertEquals(teamOwnerResponseArgument.getName(), argumentName);

                // Delete argument as testUser
                SecurityContextHolder.getContext().setAuthentication(token);
                assertDoesNotThrow(() -> argumentService.deleteArgument(argId));
            }
        }

        @DisplayName("Test team add/remove cards")
        @Test
        public void testTeamAddRemoveCards() {
            for (int i = 0; i < 25; i++) {
                // Create card as teamOwner
                SecurityContextHolder.getContext().setAuthentication(teamOwnerToken);
                final CardCreateRequest newCard = CardServiceTest.generateMockCard(testEmail);
                final String cardId = cardService.createCard(newCard);

                // Create argument as testUser
                SecurityContextHolder.getContext().setAuthentication(token);
                final String argumentName = RandomStringUtils.randomAlphanumeric(1, 128);
                final String argId =  argumentService.createArgument(new ArgumentCreateRequest(argumentName));

                // Add the card as teamOwner
                SecurityContextHolder.getContext().setAuthentication(teamOwnerToken);
                assertDoesNotThrow(() -> argumentService.addCardToArgument(argId, cardId));

                // Get the argument as testUser
                SecurityContextHolder.getContext().setAuthentication(token);
                final ResponseArgument responseArgument = argumentService.getResponseArgumentById(argId);
                assertEquals(argumentName, responseArgument.getName());
                assertEquals(1, responseArgument.getCards().size());
                final ResponseCard returnedCard = responseArgument.getCards().get(0);
                assertTrue(CardServiceTest.createRequestEqualsResponse(newCard, returnedCard));

                // Remove the card from the argument as teamOwner
                SecurityContextHolder.getContext().setAuthentication(teamOwnerToken);
                assertDoesNotThrow(() -> argumentService.removeCardFromArgument(argId, cardId, (short) 0));
                assertEquals(0, argumentService.getResponseArgumentById(argId).getCards().size());
                // Delete the card and argument as testUser
                SecurityContextHolder.getContext().setAuthentication(token);
                cardService.deleteCardById(cardId);
                argumentService.deleteArgument(argId);
            }
            SecurityContextHolder.getContext().setAuthentication(token);
            for (int i = 0; i < 10; i++) {
                // Create an argument as testUser
                final String argId = argumentService.createArgument(new ArgumentCreateRequest(RandomStringUtils.randomAlphanumeric(1, 128)));
                final String[] cardIds = new String[25];
                // Add cards to the argument as teamOwner
                SecurityContextHolder.getContext().setAuthentication(teamOwnerToken);
                for (int j = 0; j < 25; j++) {
                    final CardCreateRequest newCard = CardServiceTest.generateMockCard(testEmail);
                    final String cardId = cardService.createCard(newCard);
                    cardIds[j] = cardId;
                    assertDoesNotThrow(() -> argumentService.addCardToArgument(argId, cardId));
                }
                // Remove and delete the cards as testUser
                SecurityContextHolder.getContext().setAuthentication(token);
                for (short j = 25; j > 0; j--) {
                    assertEquals(j, argumentService.getResponseArgumentById(argId).getCards().size());
                    argumentService.removeCardFromArgument(argId, cardIds[j - 1], (short) (j - 1));
                    cardService.deleteCardById(cardIds[j - 1]);
                }
                assertEquals(0, argumentService.getResponseArgumentById(argId).getCards().size());
                // Delete the argument as testUser
                argumentService.deleteArgument(argId);
            }
        }

        @DisplayName("Test team create/delete argument")
        @Test
        public void testTeamCreateDeleteArgument() {
            for (int i = 0; i < 25; i++) {
                // Create an argument as testUser
                SecurityContextHolder.getContext().setAuthentication(token);
                final String argId = argumentService.createArgument(new ArgumentCreateRequest(RandomStringUtils.randomAlphanumeric(25)));

                // Delete the argument as teamOwner
                SecurityContextHolder.getContext().setAuthentication(teamOwnerToken);
                assertDoesNotThrow(() -> argumentService.deleteArgument(argId));
            }
            for (int i = 0; i < 25; i++) {
                // Create an argument as teamOwner
                SecurityContextHolder.getContext().setAuthentication(teamOwnerToken);
                final String argId = argumentService.createArgument(new ArgumentCreateRequest(RandomStringUtils.randomAlphanumeric(25)));

                // Delete the argument as testUser
                SecurityContextHolder.getContext().setAuthentication(token);
                assertDoesNotThrow(() -> argumentService.deleteArgument(argId));
            }
        }

        @DisplayName("Test team renaming")
        @Test
        public void testTeamRenaming() {
            for (int i = 0; i < 10; i++) {
                // Create an argument as testUser
                final String argumentName = RandomStringUtils.randomAlphanumeric(25);
                final String argId = argumentService.createArgument(new ArgumentCreateRequest(argumentName));
                // Rename it as teamOwner
                SecurityContextHolder.getContext().setAuthentication(teamOwnerToken);
                for (int j = 0; j < 25; j++) {
                    final String newArgumentName = RandomStringUtils.randomAlphanumeric(25);
                    assertDoesNotThrow(() -> argumentService.renameArgument(argId, newArgumentName));
                    final ResponseArgument responseArgument = argumentService.getResponseArgumentById(argId);
                    assertEquals(newArgumentName, responseArgument.getName());
                }
                // Delete the argument as testUser
                SecurityContextHolder.getContext().setAuthentication(token);
                assertDoesNotThrow(() -> argumentService.deleteArgument(argId));
            }
        }

        @DisplayName("Test team reordering cards in argument")
        @Test
        public void testReorderCards() {
            // Create argument and add new cards to argument as testUser
            final String argumentId = argumentService.createArgument(new ArgumentCreateRequest(RandomStringUtils.randomAlphanumeric(1, 129)));
            final List<String> cardIds = new ArrayList<>(32);
            for (int i = 0; i < 25; i++) {
                final CardCreateRequest newCard = CardServiceTest.generateMockCard(testEmail);
                final String cardId = cardService.createCard(newCard);
                cardIds.add(cardId);
                assertDoesNotThrow(() -> argumentService.addCardToArgument(argumentId, cardId));
            }

            // Test switching one index at a time as teamOwner
            SecurityContextHolder.getContext().setAuthentication(teamOwnerToken);
            for (int i = 0; i < 100; i++) {
                final short oldIndex = (short) RandomUtils.nextInt(0, cardIds.size());
                final short newIndex = (short) RandomUtils.nextInt(0, cardIds.size());
                final String tempId = cardIds.get(oldIndex);
                cardIds.remove(oldIndex);
                cardIds.add(newIndex, tempId);
                argumentService.updateCardPositions(argumentId, newIndex, oldIndex);
                final List<String> updatedPositions = argumentService.getResponseArgumentById(argumentId)
                        .getCards().stream()
                        .map(ResponseCard::getId)
                        .collect(Collectors.toList());
                assertEquals(cardIds.size(), updatedPositions.size());
                for (int j = 0; j < cardIds.size(); j++) {
                    assertEquals(cardIds.get(j), updatedPositions.get(j));
                }
            }

            // Clean up as testUser
            SecurityContextHolder.getContext().setAuthentication(token);
            for (String cardId : cardIds) {
                cardService.deleteCardById(cardId);
            }
            assertDoesNotThrow(() -> argumentService.deleteArgument(argumentId));
        }

        @DisplayName("Test team get argument previews by card")
        @Test
        public void testGetArgumentPreviewsByCard() {
            for (int i = 0; i < 10; i++) {
                // Create card and arguments as testUser
                final CardCreateRequest cardCreateRequest = CardServiceTest.generateMockCard(testEmail);
                final String cardId = cardService.createCard(cardCreateRequest);
                final int numNewArgumentsToMake = RandomUtils.nextInt(0, 10);
                final List<String> argumentIds = new ArrayList<>();
                for (int j = 0; j < numNewArgumentsToMake; j++) {
                    final String argumentId = argumentService.createArgument(new ArgumentCreateRequest(RandomStringUtils.randomAlphanumeric(25)));
                    argumentIds.add(argumentId);
                    argumentService.addCardToArgument(argumentId, cardId);
                }
                // Get argumentPreviews as teamOwner
                SecurityContextHolder.getContext().setAuthentication(teamOwnerToken);
                final List<ArgumentWithCardModel> argumentPreviews = argumentService.getArgumentPreviewsByCardId(cardId);
                assertEquals(numNewArgumentsToMake, argumentPreviews.size());
                for (ArgumentWithCardModel argumentWithCardModel : argumentPreviews) {
                    assertTrue(argumentIds.contains(argumentWithCardModel.getId()));
                    argumentService.deleteArgument(argumentWithCardModel.getId());
                    argumentIds.remove(argumentWithCardModel.getId());
                }
                assertEquals(0, argumentIds.size());
                // Delete the card as testUser
                cardService.deleteCardById(cardId);
            }
        }

        @DisplayName("Test getting number of arguments by team")
        @Test
        public void testNumberOfArgumentsByTeam() {
            for (int i = 0; i < 10; i++) {
                final List<String> argIds = new ArrayList<>();
                for (int j = 1; j <= 25; j++) {
                    // Create argument as testUser
                    SecurityContextHolder.getContext().setAuthentication(token);
                    final ArgumentCreateRequest argumentCreateRequest = new ArgumentCreateRequest(RandomStringUtils.randomAlphanumeric(25));
                    argIds.add(argumentService.createArgument(argumentCreateRequest));
                    // Get number of arguments as teamOwner
                    SecurityContextHolder.getContext().setAuthentication(teamOwnerToken);
                    assertEquals(j, argumentService.getNumberOfArgumentsByUser());
                }
                // Delete the arguments as testUser
                SecurityContextHolder.getContext().setAuthentication(token);
                for (String argId : argIds) {
                    argumentService.deleteArgument(argId);
                }
                // Get number of arguments as teamOwner
                SecurityContextHolder.getContext().setAuthentication(token);
                assertEquals(0, argumentService.getNumberOfArgumentsByUser());
            }
        }

    }

}
