package ru.westlarry.userAccount.exception;

public class NonUniqueException extends Exception {
    public NonUniqueException() {
        super();
    }

    public NonUniqueException(String message) {
        super(message);
    }
}
