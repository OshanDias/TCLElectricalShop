package com.tclelectrical.exception;

/**
 * User-defined (custom) exception.
 * Thrown when a sale requests more quantity than available stock.
 */
public class InsufficientStockException extends Exception {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String productName, int available, int requested) {
        super("Not enough stock for \"" + productName + "\". Available: "
              + available + ", Requested: " + requested);
    }
}