package com.zackmurry.cardtown.dao.action;

import com.zackmurry.cardtown.exception.InternalServerException;
import com.zackmurry.cardtown.model.action.ActionEntity;
import com.zackmurry.cardtown.model.action.ActionType;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
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
public class ActionDataAccessService implements ActionDao {

    private final JdbcTemplate jdbcTemplate;

    public ActionDataAccessService(DataSource dataSource) throws SQLException {
        this.jdbcTemplate = new JdbcTemplate(dataSource.getConnection());
    }

    @Override
    public void createAction(@NonNull ActionEntity actionEntity) {
        final String sql = "INSERT INTO actions (subject_id, action_type, time, user_id, card_id, argument_id) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, actionEntity.getSubjectId());
            preparedStatement.setString(2, actionEntity.getActionType().toString());
            preparedStatement.setLong(3, actionEntity.getTime());
            preparedStatement.setObject(4, actionEntity.getUserId());
            preparedStatement.setObject(5, actionEntity.getCardId());
            preparedStatement.setObject(6, actionEntity.getArgumentId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public List<ActionEntity> getActionsByTeam(@NonNull UUID teamId) {
        final String sql = "SELECT id, subject_id, action_type, time, user_id, card_id, argument_id FROM actions INNER JOIN team_members ON team_members.user_id = subject_id WHERE team_members.team_id = ? ORDER BY time DESC";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, teamId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<ActionEntity> actionEntities = new ArrayList<>();
            while (resultSet.next()) {
                actionEntities.add(
                        new ActionEntity(
                                UUID.fromString(resultSet.getString("id")),
                                UUID.fromString(resultSet.getString("subject_id")),
                                ActionType.valueOf(resultSet.getString("action_type")),
                                resultSet.getLong("time"),
                                UUID.fromString(resultSet.getString("user_id")),
                                UUID.fromString(resultSet.getString("card_id")),
                                UUID.fromString(resultSet.getString("argument_id"))
                        )
                );
            }
            return actionEntities;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public List<ActionEntity> getRecentActionsByTeam(UUID teamId, int count) {
        return getRecentActionsByTeam(teamId, count, 0);
    }

    @Override
    public List<ActionEntity> getRecentActionsByTeam(@NonNull UUID teamId, int count, int offset) {
        final String sql = "SELECT id, subject_id, action_type, time, user_id, card_id, argument_id FROM actions INNER JOIN team_members ON team_members.user_id = subject_id WHERE team_members.team_id = ? ORDER BY time DESC LIMIT ? OFFSET ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, teamId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<ActionEntity> actionEntities = new ArrayList<>();
            while (resultSet.next()) {
                actionEntities.add(
                        new ActionEntity(
                                UUID.fromString(resultSet.getString("id")),
                                UUID.fromString(resultSet.getString("subject_id")),
                                ActionType.valueOf(resultSet.getString("action_type")),
                                resultSet.getLong("time"),
                                UUID.fromString(resultSet.getString("user_id")),
                                UUID.fromString(resultSet.getString("card_id")),
                                UUID.fromString(resultSet.getString("argument_id"))
                        )
                );
            }
            return actionEntities;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

}
