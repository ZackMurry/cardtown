package com.zackmurry.cardtown.dao.card;

import com.zackmurry.cardtown.model.card.CardEntity;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

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
    public Optional<UUID> createCard(CardEntity card) {
        String sql = "INSERT INTO cards (owner_id, tag, cite, cite_information, body_html, body_draft) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            String[] returnId = { "id" };
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql, returnId);
            preparedStatement.setObject(1, card.getOwnerId());
            preparedStatement.setString(2, card.getTag());
            preparedStatement.setString(3, card.getCite());
            preparedStatement.setString(4, card.getCiteInformation());
            preparedStatement.setString(5, card.getBodyHtml());
            preparedStatement.setString(6, card.getBodyDraft());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Card creation by {} didn't generate an id.", card.getOwnerId());
            }

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return Optional.of(UUID.fromString(resultSet.getString("id")));
            }
            return Optional.empty();
        } catch(SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<CardEntity> getCardById(@NonNull UUID id) {
        String sql = "SELECT owner_id, tag, cite, cite_information, body_html, body_draft FROM cards WHERE id = ?";
        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

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
                    resultSet.getString("body_draft")
                )
            );
        } catch(SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<CardEntity> getCardsByUser(UUID id) {
        String sql = "SELECT id, tag, cite, cite_information, body_html, body_draft FROM cards WHERE owner_id = ?";
        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            List<CardEntity> cards = new ArrayList<>();
            while (rs.next()) {
                cards.add(
                        new CardEntity(
                                UUID.fromString(rs.getString("id")),
                                id,
                                rs.getString("tag"),
                                rs.getString("cite"),
                                rs.getString("cite_information"),
                                rs.getString("body_html"),
                                rs.getString("body_draft")
                        )
                );
            }
            return cards;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ResponseEntity<Integer> getNumberOfCardsByUser(UUID id) {
        String sql = "SELECT COUNT(id) FROM cards WHERE owner_id = ?";

        try {
            PreparedStatement ps = jdbcTemplate.getConnection().prepareStatement(sql);
            ps.setObject(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                return ResponseEntity.ok(resultSet.getInt("count"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
