package com.zackmurry.cardtownadminserver;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@SpringBootApplication
@EnableAdminServer
public class CardtownAdminServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardtownAdminServerApplication.class, args);
	}


}
