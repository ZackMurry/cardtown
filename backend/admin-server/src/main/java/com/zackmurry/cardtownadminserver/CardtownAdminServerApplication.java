package com.zackmurry.cardtownadminserver;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;

import java.io.*;

@SpringBootApplication
@EnableAdminServer
public class CardtownAdminServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardtownAdminServerApplication.class, args);
	}

	@Autowired
	private Environment environment;

	@Bean
	public HttpHeadersProvider httpHeadersProvider() throws IOException {
		final String adminAuthorizationHeader = "Admin " + environment.getProperty("CARDTOWN_ADMIN_USERNAME") + "|" + environment.getProperty("CARDTOWN_ADMIN_PASSWORD");
		return instance -> {
			final HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", adminAuthorizationHeader);
			return headers;
		};
	}

}
