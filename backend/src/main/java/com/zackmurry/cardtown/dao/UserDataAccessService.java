package com.zackmurry.cardtown.dao;


import com.zackmurry.cardtown.model.User;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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
        String sql = "SELECT * FROM users WHERE email=?";

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
    public HttpStatus createAccount(User user) {
        if (findByEmail(user.getEmail()).isPresent()) {
            return HttpStatus.PRECONDITION_FAILED;
        }
        String sql = "INSERT INTO users (email, first_name, last_name, password) VALUES (?, ?, ?, ?)";

        try {
            jdbcTemplate.execute(
                    sql,
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPassword() // hashed
            );
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }

}
