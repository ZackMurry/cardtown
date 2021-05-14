package com.zackmurry.cardtown.dao.arg.analytic;

import com.zackmurry.cardtown.exception.InternalServerException;
import com.zackmurry.cardtown.model.analytic.AnalyticEntity;
import com.zackmurry.cardtown.model.analytic.EncryptedAnalytic;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public void updateAnalyticById(@NonNull UUID id, @NonNull EncryptedAnalytic analytic) {
        final String sql = "UPDATE argument_analytics SET body = ? WHERE id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, analytic.getBody());
            preparedStatement.setObject(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public List<AnalyticEntity> getAnalyticsByArgumentId(@NonNull UUID id) {
        final String sql = "SELECT id, body, index_in_argument FROM argument_analytics WHERE argument_id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<AnalyticEntity> analyticEntities = new ArrayList<>();
            while (resultSet.next()) {
                analyticEntities.add(
                        new AnalyticEntity(
                                UUID.fromString(resultSet.getString("id")),
                                id,
                                resultSet.getString("body"),
                                resultSet.getShort("index_in_argument")
                        )
                );
            }
            return analyticEntities;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public short getFirstOpenIndexInArgument(@NonNull UUID argId) {
        final String sql = "SELECT index_in_argument FROM argument_analytics WHERE argument_id = ? ORDER BY index_in_argument DESC LIMIT 1";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, argId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return (short) (resultSet.getShort("index_in_argument") + 1);
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }
}
