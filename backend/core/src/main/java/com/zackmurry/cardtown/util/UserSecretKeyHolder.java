package com.zackmurry.cardtown.util;

import com.zackmurry.cardtown.exception.ForbiddenException;
import com.zackmurry.cardtown.model.auth.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This is a utility class for retrieving a secret key for a user.
 * This is only for data that the user's entire team should be able to access.
 * If data needs to be stored that should only be able to accessed by a user, the SecurityContextHolder.getContext()...
 * method chain should be preferred.
 */
public class UserSecretKeyHolder {

    private static final Logger logger = LoggerFactory.getLogger(UserSecretKeyHolder.class);

    /**
     * Gets the user's secret key that is used for encryption of data that is shared across a team
     * @return If the user is in a team: the team's secret key; else: the user's secret key
     * @throws ForbiddenException If the user is unauthenticated
     */
    public static byte[] getSecretKey() {
        final UserModel principal = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null) {
            logger.warn("Secret key requested for unauthenticated user!");
            throw new ForbiddenException();
        }
        if (principal.getTeamSecretKey() != null) {
            return principal.getTeamSecretKey();
        }
        return principal.getSecretKey();
    }

}
