package com.rest.documentManager.services.exceptions;

public class ErrorControllerException extends RuntimeException{
    public ErrorControllerException(String message) {
        super(message);
    }
}
