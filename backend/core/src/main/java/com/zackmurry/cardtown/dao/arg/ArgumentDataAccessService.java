package com.zackmurry.cardtown.dao.arg;

import com.zackmurry.cardtown.exception.ArgumentNotFoundException;
import com.zackmurry.cardtown.exception.CardNotFoundException;
import com.zackmurry.cardtown.exception.InternalServerException;
import com.zackmurry.cardtown.model.arg.ArgumentCreateRequest;
import com.zackmurry.cardtown.model.arg.ArgumentEntity;
import com.zackmurry.cardtown.model.arg.card.ArgumentCardEntity;
import com.zackmurry.cardtown.model.arg.card.ArgumentCardJoinEntity;
import com.zackmurry.cardtown.util.UUIDCompressor;
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
 * todo refactor so that card-related methods are in an ArgumentCardDao
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
            final String[] returnId = {"id"};
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
    public Optional<ArgumentEntity> getArgumentEntityById(@NonNull UUID id) {
        final String sql = "SELECT id, owner_id, name, deleted FROM arguments WHERE id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(
                        new ArgumentEntity(
                                UUID.fromString(resultSet.getString("id")),
                                UUID.fromString(resultSet.getString("owner_id")),
                                resultSet.getString("name"),
                                resultSet.getBoolean("deleted")
                        )
                );
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<ArgumentCardEntity> getCardsByArgumentId(@NonNull UUID argumentId) {
        final String sql = "SELECT card_id, index_in_argument FROM argument_cards WHERE argument_id = ? ORDER BY index_in_argument";
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
        final String sql = "SELECT index_in_argument FROM argument_cards WHERE argument_id = ? ORDER BY index_in_argument DESC LIMIT 1";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, argumentId);
            final ResultSet resultSet = preparedStatement.executeQuery();
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
        final String sql = "SELECT id, name FROM arguments WHERE owner_id = ? AND deleted = false";
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
                                resultSet.getString("name"),
                                false
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
    public List<ArgumentEntity> getArgumentsByUser(@NonNull UUID id, boolean includeDeleted) {
        if (!includeDeleted) {
            return getArgumentsByUser(id);
        }
        final String sql = "SELECT id, name, deleted FROM arguments WHERE owner_id = ?";
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
                                resultSet.getString("name"),
                                resultSet.getBoolean("deleted")
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
        final String sql = "SELECT COUNT(id) FROM arguments WHERE owner_id = ? AND deleted = FALSE";
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
    public void incrementCardPositionsInArgumentAtOrPastIndex(@NonNull UUID argumentId, short index) {
        final String sql = "UPDATE argument_cards SET index_in_argument = index_in_argument + 1 WHERE argument_id = ? AND index_in_argument >= ?";
        try {
            final PreparedStatement incrementStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            incrementStatement.setObject(1, argumentId);
            incrementStatement.setShort(2, index);
            incrementStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public void addCardToArgument(@NonNull UUID argumentId, @NonNull UUID cardId, short indexInArgument) {
        final String sql = "INSERT INTO argument_cards (argument_id, card_id, index_in_argument) VALUES (?, ?, ?)";
        try {
            final PreparedStatement insertStatement = jdbcTemplate.getConnection().prepareStatement(sql);
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
    public short getNumberOfCardsInArgument(@NonNull UUID argumentId) {
        final String sql = "SELECT COUNT(*) FROM argument_cards WHERE argument_id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, argumentId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getShort("count");
            }
            throw new InternalServerException();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public void decrementCardPositionsInArgumentAtOrPastIndex(@NonNull UUID argumentId, @NonNull short index) {
        final String sql = "UPDATE argument_cards SET index_in_argument = index_in_argument - 1 WHERE argument_id = ? AND index_in_argument >= ?";
        try {
            final PreparedStatement decrementStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            decrementStatement.setObject(1, argumentId);
            decrementStatement.setShort(2, index);
            decrementStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public void removeCardFromArgument(@NonNull UUID argumentId, @NonNull UUID cardId, short index) {
        final String sql = "DELETE FROM argument_cards WHERE argument_id = ? AND card_id = ? AND index_in_argument = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, argumentId);
            preparedStatement.setObject(2, cardId);
            preparedStatement.setShort(3, index);
            if (preparedStatement.executeUpdate() == 0) {
                logger.warn("ArgumentDataAccessService#removeCardFromArgument removed 0 cards; params: ({}, {}, {})", UUIDCompressor.compress(argumentId), UUIDCompressor.compress(cardId), index);
                // Since this should never happen at the DAO layer, throw an exception
                throw new InternalServerException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public void markArgumentAsDeleted(@NonNull UUID argumentId) {
        final String sql = "UPDATE arguments SET deleted = TRUE WHERE id = ?";
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

    @Override
    public void setCardIndexInArgumentUnchecked(UUID argumentId, short newIndex, short oldIndex) {
        final String sql = "UPDATE argument_cards SET index_in_argument = ? WHERE argument_id = ? AND index_in_argument = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setShort(1, newIndex);
            preparedStatement.setObject(2, argumentId);
            preparedStatement.setShort(3, oldIndex);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public List<ArgumentCardEntity> getArgumentCardEntitiesByCardId(@NonNull UUID cardId) {
        final String sql = "SELECT argument_id, index_in_argument FROM argument_cards AS ac INNER JOIN arguments as a ON a.id = ac.argument_id WHERE ac.card_id = ? AND a.deleted = FALSE";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, cardId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<ArgumentCardEntity> argumentCards = new ArrayList<>();
            while (resultSet.next()) {
                argumentCards.add(
                        new ArgumentCardEntity(
                                UUID.fromString(resultSet.getString("argument_id")),
                                cardId,
                                resultSet.getShort("index_in_argument")
                        )
                );
            }
            return argumentCards;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public List<ArgumentCardJoinEntity> getArgumentCardJoinEntitiesByCardId(@NonNull UUID cardId) {
        final String sql = "SELECT argument_id, index_in_argument, owner_id, name FROM argument_cards AS ac INNER JOIN arguments ON arguments.id = ac.argument_id WHERE ac.card_id = ? AND arguments.deleted = FALSE";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, cardId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<ArgumentCardJoinEntity> argumentCardJoinEntities = new ArrayList<>();
            while (resultSet.next()) {
                argumentCardJoinEntities.add(
                        new ArgumentCardJoinEntity(
                                UUID.fromString(resultSet.getString("argument_id")),
                                UUID.fromString(resultSet.getString("owner_id")),
                                resultSet.getString("name"),
                                cardId,
                                resultSet.getShort("index_in_argument")
                        )
                );
            }
            return argumentCardJoinEntities;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public int getNumberOfArgumentsByTeam(@NonNull UUID teamId) {
        final String sql = "SELECT COUNT(arguments.id) FROM arguments INNER JOIN team_members ON team_members.user_id = arguments.owner_id WHERE team_members.team_id = ? AND arguments.deleted = FALSE";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, teamId);
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
    public List<ArgumentEntity> getArgumentsByTeam(@NonNull UUID teamId) {
        final String sql = "SELECT id, owner_id, name, deleted FROM arguments INNER JOIN team_members ON team_members.user_id = arguments.owner_id WHERE team_members.team_id = ? AND deleted = FALSE";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, teamId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<ArgumentEntity> args = new ArrayList<>();
            while (resultSet.next()) {
                args.add(
                        new ArgumentEntity(
                                UUID.fromString(resultSet.getString("id")),
                                UUID.fromString(resultSet.getString("owner_id")),
                                resultSet.getString("name"),
                                resultSet.getBoolean("deleted")
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
    public List<ArgumentEntity> getArgumentsByTeam(@NonNull UUID teamId, boolean includeDeleted) {
        if (!includeDeleted) {
            return getArgumentsByTeam(teamId);
        }
        final String sql = "SELECT id, owner_id, name, deleted FROM arguments INNER JOIN team_members ON team_members.user_id = arguments.owner_id WHERE team_members.team_id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, teamId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<ArgumentEntity> args = new ArrayList<>();
            while (resultSet.next()) {
                args.add(
                        new ArgumentEntity(
                                UUID.fromString(resultSet.getString("id")),
                                UUID.fromString(resultSet.getString("owner_id")),
                                resultSet.getString("name"),
                                resultSet.getBoolean("deleted")
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
    public void restoreArgumentById(@NonNull UUID id) {
        final String sql = "UPDATE arguments SET deleted = FALSE WHERE id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            final int rowsChanged = preparedStatement.executeUpdate();
            if (rowsChanged == 0) {
                throw new ArgumentNotFoundException();
            } else if (rowsChanged > 1) {
                logger.warn("There were more than one rows changed in a statement to restore a argument.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.warn("SQL exception occurred when restoring argument {}", id);
            throw new InternalServerException();
        }

    }

    @Override
    public void deleteArgumentById(@NonNull UUID id) {
        final String sql = "DELETE FROM arguments WHERE id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public Optional<UUID> getCardIdInArgumentAtPosition(@NonNull UUID argId, short indexInArgument) {
        final String sql = "SELECT card_id FROM argument_cards WHERE argument_id = ? AND index_in_argument = ? LIMIT 1";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, argId);
            preparedStatement.setShort(2, indexInArgument);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(UUID.fromString(resultSet.getString("card_id")));
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public void incrementPositionsOfCardsInArgument(UUID argumentId, short startInclusive, short endInclusive) {
        final String sql = "UPDATE argument_cards SET index_in_argument = index_in_argument + 1 WHERE argument_id = ? AND index_in_argument >= ? AND index_in_argument <= ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, argumentId);
            preparedStatement.setShort(2, startInclusive);
            preparedStatement.setShort(3, endInclusive);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public void decrementPositionsOfCardsInArgument(UUID argumentId, short startInclusive, short endInclusive) {
        final String sql = "UPDATE argument_cards SET index_in_argument = index_in_argument - 1 WHERE argument_id = ? AND index_in_argument >= ? AND index_in_argument <= ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, argumentId);
            preparedStatement.setShort(2, startInclusive);
            preparedStatement.setShort(3, endInclusive);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }
}
