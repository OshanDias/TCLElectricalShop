package com.tclelectrical.util;

import com.tclelectrical.exception.InvalidInputException;
import java.util.regex.Pattern;

public class Validator {

    private static final Pattern EMAIL_RX =
        Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$");
    private static final Pattern PHONE_RX =
        Pattern.compile("^0\\d{9}$");                 // 10 digits, starts with 0
    private static final Pattern NIC_RX =
        Pattern.compile("^(\\d{9}[vVxX]|\\d{12})$");  // old 9+V or new 12-digit

    // ---- boolean helpers ----
    public static boolean isNotEmpty(String s) { return s != null && !s.trim().isEmpty(); }
    public static boolean isValidEmail(String s) { return isNotEmpty(s) && EMAIL_RX.matcher(s.trim()).matches(); }
    public static boolean isValidPhone(String s) { return isNotEmpty(s) && PHONE_RX.matcher(s.trim()).matches(); }
    public static boolean isValidNIC(String s)   { return isNotEmpty(s) && NIC_RX.matcher(s.trim()).matches(); }

    // ---- throwing validators (use these in forms) ----
    public static void requireText(String value, String fieldName) throws InvalidInputException {
        if (!isNotEmpty(value))
            throw new InvalidInputException(fieldName + " is required.");
    }

    public static void requirePhone(String value) throws InvalidInputException {
        if (!isValidPhone(value))
            throw new InvalidInputException("Phone must be 10 digits and start with 0 (e.g. 0771234567).");
    }

    public static void requireEmail(String value) throws InvalidInputException {
        // email optional in our schema -> only validate if typed
        if (isNotEmpty(value) && !isValidEmail(value))
            throw new InvalidInputException("Email format is invalid.");
    }

    public static void requireNIC(String value) throws InvalidInputException {
        if (isNotEmpty(value) && !isValidNIC(value))
            throw new InvalidInputException("NIC must be 9 digits + V, or 12 digits.");
    }

    /** Parses a positive number (price); throws if invalid. */
    public static double requirePositiveDouble(String value, String fieldName) throws InvalidInputException {
        try {
            double d = Double.parseDouble(value.trim());
            if (d < 0) throw new InvalidInputException(fieldName + " cannot be negative.");
            return d;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName + " must be a valid number.");
        }
    }

    /** Parses a non-negative integer (quantity, reorder level); throws if invalid. */
    public static int requireNonNegativeInt(String value, String fieldName) throws InvalidInputException {
        try {
            int i = Integer.parseInt(value.trim());
            if (i < 0) throw new InvalidInputException(fieldName + " cannot be negative.");
            return i;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName + " must be a whole number.");
        }
    }
}