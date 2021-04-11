package com.zackmurry.cardtown.dao.user;


import com.zackmurry.cardtown.exception.InternalServerException;
import com.zackmurry.cardtown.exception.UserNotFoundException;
import com.zackmurry.cardtown.model.auth.FirstLastName;
import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.model.auth.UserRole;
import org.apache.tomcat.util.codec.binary.Base64;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserDataAccessService implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDataAccessService.class);

    private final JdbcTemplate jdbcTemplate;

    public UserDataAccessService(DataSource dataSource) throws SQLException {
        this.jdbcTemplate = new JdbcTemplate(dataSource.getConnection());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        final String sql = "SELECT * FROM users WHERE email= ?";
        try {
            final List<User> users = jdbcTemplate.query(
                    sql,
                    resultSet -> new User(
                            UUID.fromString(resultSet.getString("id")),
                            resultSet.getString("email"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            resultSet.getString("password"),
                            resultSet.getString("role")
                    ),
                    email
            );
            if (!users.isEmpty()) {
                return Optional.of(users.get(0));
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public Optional<UUID> getIdByEmail(String email) {
        final String sql = "SELECT id FROM users WHERE email = ?";
        try {
            final List<Object> list = jdbcTemplate.query(
                    sql,
                    resultSet -> UUID.fromString(resultSet.getString("id")),
                    email
            );
            if (!list.isEmpty()) {
                return Optional.of((UUID) list.get(0));
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public UUID createAccount(@NonNull UserModel user) {
        if (findByEmail(user.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "User with the email already exists");
        }
        if (user.getRoles() == null) {
            logger.warn("Error creating user account: no roles defined");
            throw new InternalServerException();
        }
        if (user.getRoles().isEmpty()) {
            logger.warn("User roles is empty. Replacing...");
            user.setRoles(List.of(UserRole.USER));
        }
        final String sql = "INSERT INTO users (email, first_name, last_name, password, encrypted_secret_key, role) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            final String[] returnFields = { "id" };
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql, returnFields);
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getFirstName());
            preparedStatement.setString(3, user.getLastName());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setString(5, Base64.encodeBase64String(user.getSecretKey()));
            preparedStatement.setString(6, user.getRoles().get(0).getName());
            preparedStatement.executeUpdate();
            final ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return UUID.fromString(resultSet.getString("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new InternalServerException();
    }

    @Override
    public boolean accountExists(String email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public void deleteUser(String email) throws UserNotFoundException {
        if (!accountExists(email)) {
            throw new UserNotFoundException();
        }
        final String sql = "DELETE FROM users WHERE email = ?";
        try {
            jdbcTemplate.execute(
                    sql,
                    email
            );
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public Optional<String> getEncryptedSecretKey(String email) {
        final String sql = "SELECT encrypted_secret_key FROM users WHERE email = ?";
        try {
            return Optional.of(
                    jdbcTemplate.queryForString(
                            sql,
                            email
                    )
            );
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public Optional<User> findById(UUID id) {
        final String sql = "SELECT email, first_name, last_name, password, role FROM users WHERE id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(
                        new User(
                                id,
                                resultSet.getString("email"),
                                resultSet.getString("first_name"),
                                resultSet.getString("last_name"),
                                resultSet.getString("password"),
                                resultSet.getString("role")
                        )
                );
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public void updateUserName(@NonNull UUID id, @NonNull FirstLastName name) {
        final String sql = "UPDATE users SET first_name = ?, last_name = ? WHERE id = ?";
        try {
            final PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, name.getFirst());
            preparedStatement.setString(2, name.getLast());
            preparedStatement.setObject(3, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

}
