package com.zackmurry.cardtown.dao.card;

import com.zackmurry.cardtown.model.card.BasicCard;
import com.zackmurry.cardtown.model.card.CardEntity;
import com.zackmurry.cardtown.model.card.UUIDOwnerCard;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CardDataAccessService implements CardDao {

    private final JdbcTemplate jdbcTemplate;

    public CardDataAccessService(DataSource dataSource) throws SQLException {
        this.jdbcTemplate = new JdbcTemplate(dataSource.getConnection());
    }

    /**
     * @param card card to be created. UUID field will be ignored. all values besides UUID must be non-null
     * @return HttpStatus representing success
     */
    @Override
    public Optional<UUID> createCard(UUIDOwnerCard card) {
        String sql = "INSERT INTO cards (owner_id, tag, cite, cite_information, body) VALUES (?, ?, ?, ?, ?)";
        try {
            String[] returnId = { "id" };
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql, returnId);
            preparedStatement.setObject(1, card.getOwnerId());
            preparedStatement.setString(2, card.getTag());
            preparedStatement.setString(3, card.getCite());
            preparedStatement.setString(4, card.getCiteInformation());
            preparedStatement.setString(5, card.getBody());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("no affected rows");
            }

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return Optional.of(UUID.fromString(resultSet.getString("id")));
            }
            System.out.println("not next");
            return Optional.empty();
        } catch(SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<CardEntity> getCardById(@NonNull UUID id) {
        String sql = "SELECT owner_id, tag, cite, cite_information, body FROM cards WHERE id = ?";
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
                    resultSet.getString("body")
                )
            );
        } catch(SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

}
