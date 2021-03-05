package com.zackmurry.cardtown;

import com.zackmurry.cardtown.exception.UserNotFoundException;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.model.team.TeamCreateRequest;
import com.zackmurry.cardtown.model.team.TeamEntity;
import com.zackmurry.cardtown.service.TeamService;
import com.zackmurry.cardtown.service.UserService;
import com.zackmurry.cardtown.util.EncryptionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class TeamServiceTest {

    @Autowired
    private TeamService teamService;

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

    @AfterEach
    public void removeAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @BeforeEach
    public void setAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @DisplayName("Test creating and deleting teams")
    @Test
    public void testCreateDeleteTeams() {
        for (int i = 0; i < 25; i++) {
            final String teamName = RandomStringUtils.randomAlphanumeric(1, 128);
            teamService.createTeam(new TeamCreateRequest(teamName));

            // Refresh user context with updated team secret key
            final UserModel userModel = userService.getUserModelByEmail(
                    testEmail,
                    encryptionUtils.getSHA256Hash(
                            testPassword.getBytes(StandardCharsets.UTF_8)
                    )
            ).orElseThrow(UserNotFoundException::new);
            token = new UsernamePasswordAuthenticationToken(userModel, null, userModel.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(token);

            final Optional<TeamEntity> optionalTeamEntity = teamService.getTeamOfUser();
            assertTrue(optionalTeamEntity.isPresent());
            assertEquals(teamName, optionalTeamEntity.get().getName());
            assertDoesNotThrow(() -> teamService.deleteTeam());
            assertTrue(teamService.getTeamOfUser().isEmpty());
        }
    }

}
