package com.zackmurry.cardtown.dao.user;


import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.model.auth.UserModel;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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
    public HttpStatus createAccount(UserModel user) {
        if (findByEmail(user.getEmail()).isPresent()) {
            return HttpStatus.PRECONDITION_FAILED;
        }
        String sql = "INSERT INTO users (email, first_name, last_name, password, encrypted_secret_key) VALUES (?, ?, ?, ?, ?)";

        try {
            jdbcTemplate.execute(
                    sql,
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPassword(), // hashed
                    user.getSecretKey() // encrypted with AES by SHA-256 hash of password
            );
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }

    @Override
    public boolean accountExists(String email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public HttpStatus deleteUser(String email) {
        if (!accountExists(email)) {
            return HttpStatus.NOT_FOUND;
        }
        String sql = "DELETE FROM users WHERE email = ?";
        try {
            jdbcTemplate.execute(
                    sql,
                    email
            );
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
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
            return null;
        }
    }

}
