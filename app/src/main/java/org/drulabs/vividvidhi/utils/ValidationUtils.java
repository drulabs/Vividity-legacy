package org.drulabs.vividvidhi.utils;

import org.drulabs.vividvidhi.config.Constants;

/**
 * Created by kaushald on 26/01/17.
 */

public class ValidationUtils {

    private static final String ALPHA_NUMERIC_REGEX = "^[a-zA-Z0-9]*$";
    private static final String ALPHABETIC_REGEX = "^[a-zA-Z ]*$";
    private static final String NUMERIC_REGEX = "^[0-9]*$";

    public static boolean isValidUserName(String username) {
        boolean isValid = username != null && username.trim().length() >= Constants
                .MIN_USERNAME_LENGTH && username.matches(ALPHA_NUMERIC_REGEX);
        return isValid;
    }

    public static boolean isValidPassword(String password) {
        boolean isValid = password != null && password.trim().length() >= Constants
                .MIN_PASSWORD_LENGTH && password.matches(NUMERIC_REGEX);
        return isValid;
    }

    public static boolean isValidName(String name) {
        boolean isValid = name != null && name.trim().length() >= Constants
                .MIN_NAME_LENGTH && name.matches(ALPHABETIC_REGEX);
        return isValid;
    }
}
