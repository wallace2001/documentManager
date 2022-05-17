package com.rest.documentManager.services.exceptions;

public class ForbidenException extends RuntimeException{
    public ForbidenException(String message) {
        super(message);
    }
}
