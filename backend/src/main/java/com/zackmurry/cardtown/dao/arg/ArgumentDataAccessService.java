package com.zackmurry.cardtown.dao.arg;

import com.zackmurry.cardtown.exception.ArgumentNotFoundException;
import com.zackmurry.cardtown.exception.CardNotFoundException;
import com.zackmurry.cardtown.exception.InternalServerException;
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
 * todo unit tests -_-
 * todo: when a card is deleted, the argument indices don't update
 */
@Repository
public class ArgumentDataAccessService implements ArgumentDao {

    private static final Logger logger = LoggerFactory.getLogger(ArgumentDataAccessService.class);

    private final JdbcTemplate jdbcTemplate;

    public ArgumentDataAccessService(DataSource dataSource) throws SQLException {
        this.jdbcTemplate = new JdbcTemplate(dataSource.getConnection());
    }

    @Override
    public UUID createArgument(@NonNull ArgumentCreateRequest request) {
        final String sql = "INSERT INTO arguments (owner_id, name) VALUES (?, ?)";
        try {
            final String[] returnId = { "id" };
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql, returnId);
            preparedStatement.setObject(1, request.getOwnerId());
            preparedStatement.setString(2, request.getName());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Argument creation by {} didn't generate an id", request.getOwnerId());
                throw new InternalServerException();
            }

            final ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return UUID.fromString(resultSet.getString("id"));
            } else {
                logger.warn("Argument creation by {} didn't return a result set with values", request.getOwnerId());
                throw new InternalServerException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public Optional<ArgumentEntity> getArgumentEntity(@NonNull UUID id) {
        final String sql = "SELECT id, owner_id, name FROM arguments WHERE id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
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
        final String sql = "SELECT card_id, index_in_argument FROM argument_cards WHERE argument_id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, argumentId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<ArgumentCardEntity> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(new ArgumentCardEntity(argumentId, UUID.fromString(resultSet.getString("card_id")), resultSet.getShort("index_in_argument")));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public short getFirstOpenIndexInArgument(@NonNull UUID argumentId) {
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
    public List<ArgumentEntity> getArgumentsByUser(@NonNull UUID id) {
        final String sql = "SELECT id, name FROM arguments WHERE owner_id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<ArgumentEntity> args = new ArrayList<>();
            while (resultSet.next()) {
                args.add(
                        new ArgumentEntity(
                                UUID.fromString(resultSet.getString("id")),
                                id,
                                resultSet.getString("name")
                        )
                );
            }
            return args;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public int getNumberOfArgumentsByUser(@NonNull UUID id) {
        final String sql = "SELECT COUNT(id) FROM arguments WHERE owner_id = ?";
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
    public void addCardToArgument(@NonNull UUID cardId, @NonNull UUID argumentId, short indexInArgument) {
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

    @Override
    public int getNumberOfCardsInArgument(@NonNull UUID argumentId) {
        final String sql = "SELECT COUNT(card_id) FROM argument_cards WHERE argument_id = ?";
        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, argumentId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
            throw new InternalServerException();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public void removeCardFromArgument(@NonNull UUID argumentId, @NonNull UUID cardId) {
        final short indexInArgument = getIndexOfCardInArgument(argumentId, cardId);
        final String removeSql = "DELETE FROM argument_cards WHERE argument_id = ? AND card_id = ?";
        final String incrementIndexSql = "UPDATE argument_cards SET index_in_argument = index_in_argument - 1 WHERE argument_id = ? AND index_in_argument >= ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(removeSql);
            preparedStatement.setObject(1, argumentId);
            preparedStatement.setObject(2, cardId);
            preparedStatement.executeUpdate();
            final PreparedStatement incrementStatement = jdbcTemplate.getConnection().prepareStatement(incrementIndexSql);
            incrementStatement.setObject(1, argumentId);
            incrementStatement.setShort(2, indexInArgument);
            incrementStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public short getIndexOfCardInArgument(@NonNull UUID argumentId, @NonNull UUID cardId) {
        final String sql = "SELECT index_in_argument FROM argument_cards WHERE argument_id = ? AND card_id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, argumentId);
            preparedStatement.setObject(2, cardId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getShort("index_in_argument");
            }
            throw new CardNotFoundException();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public void deleteArgument(@NonNull UUID argumentId) {
        // This will cascade to argument_cards
        final String sql = "DELETE FROM arguments WHERE id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, argumentId);
            int rowsRemoved = preparedStatement.executeUpdate();
            if (rowsRemoved == 0) {
                throw new ArgumentNotFoundException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public void renameArgument(@NonNull UUID argumentId, @NonNull String newName) {
        final String sql = "UPDATE arguments SET name = ? WHERE id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, newName);
            preparedStatement.setObject(2, argumentId);
            int rowsChanged = preparedStatement.executeUpdate();
            if (rowsChanged == 0) {
                throw new ArgumentNotFoundException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }
}
