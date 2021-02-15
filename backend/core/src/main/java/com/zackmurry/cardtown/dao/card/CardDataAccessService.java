package com.zackmurry.cardtown.dao.card;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        final String sql = "INSERT INTO cards (owner_id, tag, cite, cite_information, body_html, body_draft, body_text) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            final String[] returnId = { "id" };
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql, returnId);
            preparedStatement.setObject(1, card.getOwnerId());
            preparedStatement.setString(2, card.getTag());
            preparedStatement.setString(3, card.getCite());
            preparedStatement.setString(4, card.getCiteInformation());
            preparedStatement.setString(5, card.getBodyHtml());
            preparedStatement.setString(6, card.getBodyDraft());
            preparedStatement.setString(7, card.getBodyText());
            final int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Card creation by {} didn't generate an id.", card.getOwnerId());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            final ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return UUID.fromString(resultSet.getString("id"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        throw new InternalServerException();
    }

    @Override
    public Optional<CardEntity> getCardById(@NonNull UUID id) {
        final String sql = "SELECT owner_id, tag, cite, cite_information, body_html, body_draft, body_text FROM cards WHERE id = ?";
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
                    resultSet.getString("body_text")
                )
            );
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public List<CardEntity> getCardsByUser(@NonNull UUID id) {
        final String sql = "SELECT id, tag, cite, cite_information, body_html, body_draft, body_text FROM cards WHERE owner_id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            final ResultSet rs = preparedStatement.executeQuery();

            final List<CardEntity> cards = new ArrayList<>();
            while (rs.next()) {
                cards.add(
                        new CardEntity(
                                UUID.fromString(rs.getString("id")),
                                id,
                                rs.getString("tag"),
                                rs.getString("cite"),
                                rs.getString("cite_information"),
                                rs.getString("body_html"),
                                rs.getString("body_draft"),
                                rs.getString("body_text")
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
        final String sql = "SELECT COUNT(id) FROM cards WHERE owner_id = ?";
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
    public void deleteCardById(@NonNull UUID id) {
        final String sql = "DELETE FROM cards WHERE id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            final int rowsChanged = preparedStatement.executeUpdate();
            if (rowsChanged == 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
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
        final String sql = "UPDATE cards SET tag = ?, cite = ?, cite_information = ?, body_html = ?, body_draft = ?, body_text = ? WHERE id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, request.getTag());
            preparedStatement.setString(2, request.getCite());
            preparedStatement.setString(3, request.getCiteInformation());
            preparedStatement.setString(4, request.getBodyHtml());
            preparedStatement.setString(5, request.getBodyDraft());
            preparedStatement.setString(6, request.getBodyText());
            preparedStatement.setObject(7, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.warn("SQL exception occurred when editing card {}", id);
            throw new InternalServerException();
        }
    }

}
