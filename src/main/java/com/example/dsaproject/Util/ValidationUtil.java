package com.example.dsaproject.Util;

import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtil {

    // Regex patterns
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9]{10,11}$");

    private static final Pattern LICENSE_PATTERN =
            Pattern.compile("^[A-Z0-9]{5,15}$");

    private static final Pattern BUS_NO_PATTERN =
            Pattern.compile("^[A-Z0-9-]{3,10}$");

    /**
     * Validate email address
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate phone number (10-11 digits)
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    /**
     * Validate license number (5-15 alphanumeric)
     */
    public static boolean isValidLicenseNo(String license) {
        if (license == null || license.trim().isEmpty()) {
            return false;
        }
        return LICENSE_PATTERN.matcher(license.toUpperCase()).matches();
    }

    /**
     * Validate bus number
     */
    public static boolean isValidBusNo(String busNo) {
        if (busNo == null || busNo.trim().isEmpty()) {
            return false;
        }
        return BUS_NO_PATTERN.matcher(busNo.toUpperCase()).matches();
    }

    /**
     * Validate name (not empty, 2-100 characters)
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        int length = name.trim().length();
        return length >= 2 && length <= 100;
    }

    /**
     * Validate password (minimum 6 characters)
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Validate capacity (positive integer, reasonable range)
     */
    public static boolean isValidCapacity(int capacity) {
        return capacity > 0 && capacity <= 100;
    }

    /**
     * Validate number of stops
     */
    public static boolean isValidStopCount(int stops) {
        return stops >= 2 && stops <= 50;
    }

    /**
     * Check if string is empty or null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Check if any string in array is empty
     */
    public static boolean isAnyEmpty(String... strings) {
        for (String str : strings) {
            if (isEmpty(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sanitize string (remove extra spaces)
     */
    public static String sanitize(String str) {
        if (str == null) {
            return "";
        }
        return str.trim().replaceAll("\\s+", " ");
    }

    /**
     * Format phone number (add dashes)
     */
    public static String formatPhone(String phone) {
        if (phone == null) {
            return "";
        }
        String clean = phone.replaceAll("[^0-9]", "");
        if (clean.length() == 11) {
            return clean.substring(0, 4) + "-" + clean.substring(4, 11);
        }
        return clean;
    }
}