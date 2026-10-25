package net.engineeringdigest.journalApp.exception;

public class GoogleAuthenticationException extends RuntimeException {

    public GoogleAuthenticationException(String message) {
        super(message);
    }
}