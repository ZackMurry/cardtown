package com.zackmurry.cardtown;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CardtownApplicationTests {

	@Value("app.datasource.username")
	private String databaseUsername;

	@Value("app.datasource.password")
	private String databasePassword;

	@Test
	void contextLoads() {
		System.out.println(databaseUsername + "; " + databasePassword);
	}

}
