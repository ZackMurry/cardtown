package com.zackmurry.cardtown;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class DatasourceTest {

    @Value("app.datasource.username")
    private String databaseUsername;

    @Value("app.datasource.password")
    private String databasePassword;

    @DisplayName("Test database username environment variable")
    @Test
    public void testDatabaseUsername() {
        assertNotNull(databaseUsername);
    }

    @DisplayName("Test database password environment variable")
    @Test
    public void testDatabasePassword() {
        assertNotNull(databasePassword);
    }
}
