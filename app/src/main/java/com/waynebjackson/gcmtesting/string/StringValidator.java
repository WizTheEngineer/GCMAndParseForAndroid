package com.waynebjackson.gcmtesting.string;

/**
 * Created by Wayne on 11/7/15.
 */
public class StringValidator {

    // Checks if string is null or empty.
    public static boolean isNullOrEmpty(String string) {
        if (string == null) {
            return true;
        }

        if (string.trim().length() == 0) {
            return true;
        }

        return false;
    }

    // Checks if any strings in a series are null or empty
    public static boolean isNullOrEmpty(String... strings) {
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];

            if (isNullOrEmpty(string)) {
                return true;
            }
        }

        return false;
    }

    // Checks if email address is in valid format.
    public static boolean isValidEmail(String emailAddress) {
        return emailAddress.contains(" ") == false && emailAddress.matches(".+@.+\\.[a-z]+");
    }

    // Checks if two strings match one another
    public static boolean matches(String stringOne, String stringTwo) {
        return stringOne.equals(stringTwo);
    }
}
