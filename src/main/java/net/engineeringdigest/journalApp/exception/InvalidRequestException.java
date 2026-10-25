package net.engineeringdigest.journalApp.exception;

public class InvalidRequestException
        extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
