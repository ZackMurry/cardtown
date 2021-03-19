package com.zackmurry.cardtown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CardtownApplication {

    public static void main(String[] args) {
        // todo rate limiting
        // todo delete test users after all tests (in case any tests failed)
        // todo unit tests with expected ForbiddenException
        // todo better integrate deleted cards/args into the frontend
        System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
        SpringApplication.run(CardtownApplication.class, args);
    }

}
