package com.zackmurry.cardtown.dao.user;


import com.zackmurry.cardtown.exception.InternalServerException;
import com.zackmurry.cardtown.exception.UserNotFoundException;
import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.model.auth.UserModel;
import org.apache.tomcat.util.codec.binary.Base64;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    private final JdbcTemplate jdbcTemplate;

    public UserDataAccessService(DataSource dataSource) throws SQLException {
        this.jdbcTemplate = new JdbcTemplate(dataSource.getConnection());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email= ?";

        try {
            List<User> users = jdbcTemplate.query(
                    sql,
                    resultSet -> new User(
                            UUID.fromString(resultSet.getString("id")),
                            resultSet.getString("email"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            resultSet.getString("password")
                    ),
                    email
            );
            if (users.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(users.get(0));
        } catch(SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<UUID> getIdByEmail(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try {
            List<Object> list = jdbcTemplate.query(
                    sql,
                    resultSet -> UUID.fromString(resultSet.getString("id")),
                    email
            );
            if (list.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of((UUID) list.get(0));
        } catch(SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void createAccount(UserModel user) {
        if (findByEmail(user.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "User with the email already exists");
        }
        String sql = "INSERT INTO users (email, first_name, last_name, password, encrypted_secret_key) VALUES (?, ?, ?, ?, ?)";

        try {
            jdbcTemplate.execute(
                    sql,
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPassword(), // hashed
                    Base64.encodeBase64String(user.getSecretKey()) // encrypted with AES by SHA-256 hash of password
            );
        } catch(SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }

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
        String sql = "DELETE FROM users WHERE email = ?";
        try {
            jdbcTemplate.execute(
                    sql,
                    email
            );
        } catch(SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public String getEncryptedSecretKey(String email) {
        String sql = "SELECT encrypted_secret_key FROM users WHERE email = ?";
        try {
            return jdbcTemplate.queryForString(
                    sql,
                    email
            );
        } catch(SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    @Override
    public Optional<User> findById(UUID id) {
        String sql = "SELECT email, first_name, last_name, password FROM users WHERE id = ?";
        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(
                        new User(
                                id,
                                resultSet.getString("email"),
                                resultSet.getString("first_name"),
                                resultSet.getString("last_name"),
                                resultSet.getString("password")
                        )
                );
            }
            return Optional.empty();
        } catch(SQLException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

}
