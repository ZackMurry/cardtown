package com.zackmurry.cardtown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class CardtownApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardtownApplication.class, args);
	}

}
