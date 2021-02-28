package com.zackmurry.cardtown.dao.team;

import com.zackmurry.cardtown.exception.InternalServerException;
import com.zackmurry.cardtown.model.team.TeamCreateRequest;
import com.zackmurry.cardtown.model.team.TeamEntity;
import com.zackmurry.cardtown.model.team.TeamMemberEntity;
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

@Repository
public class TeamDataAccessService implements TeamDao {

    private static final Logger logger = LoggerFactory.getLogger(TeamDataAccessService.class);

    private final JdbcTemplate jdbcTemplate;

    public TeamDataAccessService(DataSource dataSource) throws SQLException {
        this.jdbcTemplate = new JdbcTemplate(dataSource.getConnection());
    }

    @Override
    public UUID createTeam(@NonNull TeamEntity request) {
        final String sql = "INSERT INTO teams (name, secret_key_hash) VALUES (?, ?)";
        try {
            final String[] returnId = { "id" };
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql, returnId);
            preparedStatement.setString(1, request.getName());
            preparedStatement.setString(2, request.getSecretKeyHash());
            preparedStatement.executeUpdate();
            final ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return UUID.fromString(resultSet.getString("id"));
            }
            logger.warn("Team creation didn't generate an id");
            throw new InternalServerException();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public void addMemberToTeam(@NonNull TeamMemberEntity entity) {
        final String sql = "INSERT INTO team_members (team_id, user_id, team_secret_key, role) VALUES (?, ?, ?, ?)";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, entity.getTeamId());
            preparedStatement.setObject(2, entity.getUserId());
            preparedStatement.setString(3, entity.getTeamSecretKey());
            preparedStatement.setString(4, entity.getRole().getName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public Optional<TeamEntity> getTeamById(@NonNull UUID id) {
        final String sql = "SELECT * FROM teams WHERE id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(
                        new TeamEntity(
                                resultSet.getString("name"),
                                id,
                                resultSet.getString("secret_key_hash")
                        )
                );
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

}
