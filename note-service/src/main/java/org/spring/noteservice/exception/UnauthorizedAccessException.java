package org.spring.noteservice.exception;

public class UnauthorizedAccessException extends RuntimeException{

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
