package com.zackmurry.cardtown.util;

import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.model.auth.UserRole;
import com.zackmurry.cardtown.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountCreator implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(AdminAccountCreator.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Environment environment;

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent contextRefreshedEvent) {
        final String adminUsername = environment.getProperty("CARDTOWN_ADMIN_USERNAME");
        final String adminPassword = environment.getProperty("CARDTOWN_ADMIN_PASSWORD");
        if (!userService.accountExists(adminUsername)) {
            logger.info("No admin account found: creating new account with username {} and password {}", adminUsername, adminPassword);
            userService.createUserAccount(new User(
                    adminUsername,
                    "Admin",
                    "User",
                    adminPassword,
                    UserRole.ADMIN
            ));
        }
    }

}
