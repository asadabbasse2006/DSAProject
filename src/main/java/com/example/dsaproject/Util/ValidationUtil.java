package com.example.dsaproject.Util;

import java.util.regex.Pattern;

/**
 * Validation Utility Class
 * Provides input validation methods
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9]{4}-[0-9]{7}$");

    private static final Pattern BUS_NO_PATTERN =
            Pattern.compile("^[A-Z]-[0-9]{3}$");

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidBusNo(String busNo) {
        if (busNo == null || busNo.trim().isEmpty()) {
            return false;
        }
        return BUS_NO_PATTERN.matcher(busNo).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isPositiveNumber(String value) {
        try {
            int num = Integer.parseInt(value);
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
