package net.engineeringdigest.journalApp.exception;

public class DuplicateKeyException extends RuntimeException{
    public DuplicateKeyException(String message){
        super(message);
    }
}
