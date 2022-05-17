package com.rest.documentManager.services.exceptions;

public class ErrorLoginException extends RuntimeException{
    public ErrorLoginException(String message) {
        super(message);
    }
}
