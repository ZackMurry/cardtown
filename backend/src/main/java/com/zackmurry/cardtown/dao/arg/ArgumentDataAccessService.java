package com.zackmurry.cardtown.dao.arg;

import com.zackmurry.cardtown.model.arg.ArgumentCreateRequest;
import com.zackmurry.cardtown.model.arg.ArgumentEntity;
import com.zackmurry.cardtown.model.arg.card.ArgumentCardEntity;
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

/**
 * arguments are in a many-to-many relationship with cards.
 * there are two tables for arguments: arguments (meta-data about the argument) and argument_cards (links card ids to arguments, including the order they occur in)
 */
@Repository
public class ArgumentDataAccessService implements ArgumentDao {

    private static final Logger logger = LoggerFactory.getLogger(ArgumentDataAccessService.class);

    private final JdbcTemplate jdbcTemplate;

    public ArgumentDataAccessService(DataSource dataSource) throws SQLException {
        this.jdbcTemplate = new JdbcTemplate(dataSource.getConnection());
    }

    @Override
    public Optional<UUID> createArgument(@NonNull ArgumentCreateRequest request) {
        String sql = "INSERT INTO arguments (owner_id, name) VALUES (?, ?)";
        try {
            final String[] returnId = { "id" };
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql, returnId);
            preparedStatement.setObject(1, request.getOwnerId());
            preparedStatement.setString(2, request.getName());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Argument creation by {} didn't generate an id.", request.getOwnerId());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return Optional.of(UUID.fromString(resultSet.getString("id")));
            } else {
                logger.warn("Argument creation by {} didn't return a result set with values", request.getOwnerId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public Optional<ArgumentEntity> getArgument(@NonNull UUID id) {
        String sql = "SELECT id, owner_id, name FROM arguments WHERE id = ?";
        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(
                        new ArgumentEntity(
                                UUID.fromString(resultSet.getString("id")),
                                UUID.fromString(resultSet.getString("owner_id")),
                                resultSet.getString("name")
                        )
                );
            }
            return Optional.empty();
        } catch(SQLException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<ArgumentCardEntity> getCardsByArgumentId(@NonNull UUID argumentId) {
        String sql = "SELECT card_id, index_in_argument FROM argument_cards WHERE argument_id = ?";
        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, argumentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<ArgumentCardEntity> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(new ArgumentCardEntity(argumentId, UUID.fromString(resultSet.getString("card_id")), resultSet.getShort("index_in_argument")));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private short getFirstOpenIndexInArgument(@NonNull UUID argumentId) {
        String sql = "SELECT index_in_argument FROM argument_cards WHERE argument_id = ? ORDER BY index_in_argument DESC";
        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, argumentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return (short) (resultSet.getShort("index_in_argument") + 1);
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void addCardToArgument(@NonNull UUID cardId, @NonNull UUID argumentId) {
        short index = getFirstOpenIndexInArgument(argumentId);
        String sql = "INSERT INTO argument_cards (argument_id, card_id, index_in_argument) VALUES (?, ?, ?)";
        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, argumentId);
            preparedStatement.setObject(2, cardId);
            preparedStatement.setShort(3, index);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void addCardToArgument(UUID cardId, UUID argumentId, short indexInArgument) {
        if (indexInArgument > getFirstOpenIndexInArgument(argumentId)) {
            throw new IllegalArgumentException("Expected index of new card in argument to be <= current argument size");
        } else if (indexInArgument < 0) {
            throw new IllegalArgumentException("Expected index of new card in argument to be positive");
        }

        try {
            final String incrementIndexSql = "UPDATE argument_cards SET index_in_argument = index_in_argument + 1 WHERE argument_id = ? AND index_in_argument >= ?";
            PreparedStatement incrementStatement = jdbcTemplate.getConnection().prepareStatement(incrementIndexSql);
            incrementStatement.setObject(1, argumentId);
            incrementStatement.setShort(2, indexInArgument);
            incrementStatement.executeUpdate();

            final String insertSql = "INSERT INTO argument_cards (argument_id, card_id, index_in_argument) VALUES (?, ?, ?)";
            PreparedStatement insertStatement = jdbcTemplate.getConnection().prepareStatement(insertSql);
            insertStatement.setObject(1, argumentId);
            insertStatement.setObject(2, cardId);
            insertStatement.setShort(3, indexInArgument);
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
