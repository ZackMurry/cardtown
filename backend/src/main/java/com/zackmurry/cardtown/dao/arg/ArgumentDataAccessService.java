package com.zackmurry.cardtown.dao.arg;

import com.zackmurry.cardtown.model.arg.ArgumentCreateRequest;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                return Optional.empty();
            }

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return Optional.of(UUID.fromString(resultSet.getString("id")));
            } else {
                logger.warn("Argument creation by {} didn't return a result set with values", request.getOwnerId());
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

}
