package com.zackmurry.cardtown.dao.arg.analytic;

import com.zackmurry.cardtown.exception.InternalServerException;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Repository
public class ArgumentAnalyticDataAccessService implements ArgumentAnalyticDao {

    private static final Logger logger = LoggerFactory.getLogger(ArgumentAnalyticDataAccessService.class);

    private final JdbcTemplate jdbcTemplate;

    public ArgumentAnalyticDataAccessService(DataSource dataSource) throws SQLException {
        this.jdbcTemplate = new JdbcTemplate(dataSource.getConnection());
    }

    @Override
    public UUID createAnalytic(@NonNull UUID argumentId, @NonNull String body, short indexInArgument) {
        final String sql = "INSERT INTO argument_analytics (argument_id, body, index_in_argument) VALUES (?, ?, ?)";
        try {
            final String[] returnId = {"id"};
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql, returnId);
            preparedStatement.setObject(1, argumentId);
            preparedStatement.setString(2, body);
            preparedStatement.setShort(3, indexInArgument);
            preparedStatement.executeUpdate();

            final ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return UUID.fromString(resultSet.getString("id"));
            } else {
                throw new InternalServerException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }

    }

    @Override
    public void incrementPositionsOfAnalyticsInArgumentAtOrPastIndex(@NonNull UUID argumentId, short index) {
        final String sql = "UPDATE argument_analytics SET index_in_argument = index_in_argument + 1 WHERE argument_id = ? AND index_in_argument >= ?";
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
    public void decrementPositionsOfAnalyticsInArgumentAtOrPastIndex(@NonNull UUID argumentId, short index) {
        final String sql = "UPDATE argument_analytics SET index_in_argument = index_in_argument - 1 WHERE argument_id = ? AND index_in_argument >= ?";
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
}
