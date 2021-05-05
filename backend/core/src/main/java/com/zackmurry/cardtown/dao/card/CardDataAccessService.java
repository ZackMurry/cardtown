package com.zackmurry.cardtown.dao.card;

import com.zackmurry.cardtown.exception.CardNotFoundException;
import com.zackmurry.cardtown.exception.InternalServerException;
import com.zackmurry.cardtown.model.card.CardEntity;
import com.zackmurry.cardtown.model.card.EncryptedCard;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

@Repository
public class CardDataAccessService implements CardDao {

    private static final Logger logger = LoggerFactory.getLogger(CardDataAccessService.class);

    private final JdbcTemplate jdbcTemplate;

    public CardDataAccessService(DataSource dataSource) throws SQLException {
        this.jdbcTemplate = new JdbcTemplate(dataSource.getConnection());
    }

    /**
     * @param card card to be created. UUID field will be ignored. all values besides UUID must be non-null
     * @return HttpStatus representing success
     */
    @Override
    public UUID createCard(@NonNull CardEntity card) {
        final String sql = "INSERT INTO cards (owner_id, tag, cite, cite_information, body_html, body_draft, body_text, time_created_at, last_modified) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        final long currentTime = System.currentTimeMillis();
        try {
            final String[] returnId = {"id"};
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql, returnId);
            preparedStatement.setObject(1, card.getOwnerId());
            preparedStatement.setString(2, card.getTag());
            preparedStatement.setString(3, card.getCite());
            preparedStatement.setString(4, card.getCiteInformation());
            preparedStatement.setString(5, card.getBodyHtml());
            preparedStatement.setString(6, card.getBodyDraft());
            preparedStatement.setString(7, card.getBodyText());
            preparedStatement.setLong(8, currentTime);
            preparedStatement.setLong(9, currentTime);
            final int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Card creation by {} didn't generate an id.", card.getOwnerId());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            final ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return UUID.fromString(resultSet.getString("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new InternalServerException();
    }

    @Override
    public Optional<CardEntity> getCardById(@NonNull UUID id) {
        final String sql = "SELECT owner_id, tag, cite, cite_information, body_html, body_draft, body_text, time_created_at, last_modified, deleted FROM cards WHERE id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();

            // if card not found
            if (!resultSet.next()) {
                return Optional.empty();
            }
            return Optional.of(
                    new CardEntity(
                            id,
                            UUID.fromString(resultSet.getString("owner_id")),
                            resultSet.getString("tag"),
                            resultSet.getString("cite"),
                            resultSet.getString("cite_information"),
                            resultSet.getString("body_html"),
                            resultSet.getString("body_draft"),
                            resultSet.getString("body_text"),
                            resultSet.getLong("time_created_at"),
                            resultSet.getLong("last_modified"),
                            resultSet.getBoolean("deleted")
                    )
            );
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }

    }

    @Override
    public List<CardEntity> getCardsByUser(@NonNull UUID id) {
        final String sql = "SELECT id, tag, cite, cite_information, body_html, body_draft, body_text, time_created_at, last_modified FROM cards WHERE owner_id = ? AND deleted = FALSE";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();

            final List<CardEntity> cards = new ArrayList<>();
            while (resultSet.next()) {
                cards.add(
                        new CardEntity(
                                UUID.fromString(resultSet.getString("id")),
                                id,
                                resultSet.getString("tag"),
                                resultSet.getString("cite"),
                                resultSet.getString("cite_information"),
                                resultSet.getString("body_html"),
                                resultSet.getString("body_draft"),
                                resultSet.getString("body_text"),
                                resultSet.getLong("time_created_at"),
                                resultSet.getLong("last_modified"),
                                false
                        )
                );
            }
            return cards;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public List<CardEntity> getCardsByUser(@NonNull UUID id, boolean includeDeleted) {
        if (!includeDeleted) {
            return getCardsByUser(id);
        }
        final String sql = "SELECT id, tag, cite, cite_information, body_html, body_draft, body_text, time_created_at, last_modified, deleted FROM cards WHERE owner_id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();

            final List<CardEntity> cards = new ArrayList<>();
            while (resultSet.next()) {
                cards.add(
                        new CardEntity(
                                UUID.fromString(resultSet.getString("id")),
                                id,
                                resultSet.getString("tag"),
                                resultSet.getString("cite"),
                                resultSet.getString("cite_information"),
                                resultSet.getString("body_html"),
                                resultSet.getString("body_draft"),
                                resultSet.getString("body_text"),
                                resultSet.getLong("time_created_at"),
                                resultSet.getLong("last_modified"),
                                resultSet.getBoolean("deleted")
                        )
                );
            }
            return cards;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public int getNumberOfCardsByUser(@NonNull UUID id) {
        final String sql = "SELECT COUNT(id) FROM cards WHERE owner_id = ? AND deleted = FALSE";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new InternalServerException();
    }

    @Override
    public Optional<UUID> getOwnerIdByCardId(@NonNull UUID cardId) {
        final String sql = "SELECT owner_id FROM cards WHERE id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, cardId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(UUID.fromString(resultSet.getString("owner_id")));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.warn("SQL exception occurred when getting owner id from a card id. Card id: {}", cardId);
            throw new InternalServerException();
        }
    }

    @Override
    public void markCardAsDeleted(@NonNull UUID id) {
        final String sql = "UPDATE cards SET deleted = TRUE WHERE id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            final int rowsChanged = preparedStatement.executeUpdate();
            if (rowsChanged == 0) {
                throw new CardNotFoundException();
            } else if (rowsChanged > 1) {
                logger.warn("There were more than one rows changed in a statement to delete a card.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.warn("SQL exception occurred when deleting card {}", id);
            throw new InternalServerException();
        }
    }

    @Override
    public void updateCardById(@NonNull UUID id, @NonNull EncryptedCard request) {
        final String sql = "UPDATE cards SET tag = ?, cite = ?, cite_information = ?, body_html = ?, body_draft = ?, body_text = ?, last_modified = ? WHERE id = ?";
        final long currentTime = System.currentTimeMillis();
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, request.getTag());
            preparedStatement.setString(2, request.getCite());
            preparedStatement.setString(3, request.getCiteInformation());
            preparedStatement.setString(4, request.getBodyHtml());
            preparedStatement.setString(5, request.getBodyDraft());
            preparedStatement.setString(6, request.getBodyText());
            preparedStatement.setLong(7, currentTime);
            preparedStatement.setObject(8, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.warn("SQL exception occurred when editing card {}", id);
            throw new InternalServerException();
        }
    }

    @Override
    public List<CardEntity> getCardsByTeamId(@NonNull UUID teamId) {
        final String sql = "SELECT id, owner_id, tag, cite, cite_information, body_html, body_draft, body_text, time_created_at, last_modified FROM cards INNER JOIN team_members ON cards.owner_id = team_members.user_id WHERE team_members.team_id = ? AND deleted = FALSE";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, teamId);
            final ResultSet resultSet = preparedStatement.executeQuery();

            final List<CardEntity> cards = new ArrayList<>();
            while (resultSet.next()) {
                cards.add(
                        new CardEntity(
                                UUID.fromString(resultSet.getString("id")),
                                UUID.fromString(resultSet.getString("owner_id")),
                                resultSet.getString("tag"),
                                resultSet.getString("cite"),
                                resultSet.getString("cite_information"),
                                resultSet.getString("body_html"),
                                resultSet.getString("body_draft"),
                                resultSet.getString("body_text"),
                                resultSet.getLong("time_created_at"),
                                resultSet.getLong("last_modified"),
                                false
                        )
                );
            }
            return cards;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public List<CardEntity> getCardsByTeamId(@NonNull UUID teamId, boolean includeDeleted) {
        if (!includeDeleted) {
            return getCardsByTeamId(teamId);
        }
        final String sql = "SELECT id, owner_id, tag, cite, cite_information, body_html, body_draft, body_text, time_created_at, last_modified, deleted FROM cards INNER JOIN team_members ON cards.owner_id = team_members.user_id WHERE team_members.team_id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, teamId);
            final ResultSet resultSet = preparedStatement.executeQuery();

            final List<CardEntity> cards = new ArrayList<>();
            while (resultSet.next()) {
                cards.add(
                        new CardEntity(
                                UUID.fromString(resultSet.getString("id")),
                                UUID.fromString(resultSet.getString("owner_id")),
                                resultSet.getString("tag"),
                                resultSet.getString("cite"),
                                resultSet.getString("cite_information"),
                                resultSet.getString("body_html"),
                                resultSet.getString("body_draft"),
                                resultSet.getString("body_text"),
                                resultSet.getLong("time_created_at"),
                                resultSet.getLong("last_modified"),
                                resultSet.getBoolean("deleted")
                        )
                );
            }
            return cards;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public void restoreCardById(@NonNull UUID id) {
        final String sql = "UPDATE cards SET deleted = FALSE WHERE id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            final int rowsChanged = preparedStatement.executeUpdate();
            if (rowsChanged == 0) {
                throw new CardNotFoundException();
            } else if (rowsChanged > 1) {
                logger.warn("There were more than one rows changed in a statement to delete a card.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.warn("SQL exception occurred when restoring card {}", id);
            throw new InternalServerException();
        }
    }
}
