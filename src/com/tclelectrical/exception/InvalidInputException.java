package com.tclelectrical.exception;

/**
 * User-defined (custom) exception.
 * Thrown when user input fails validation rules.
 */
public class InvalidInputException extends Exception {

    public InvalidInputException(String message) {
        super(message);
    }
}