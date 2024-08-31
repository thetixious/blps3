package com.blps.lab3.exception.customException;

public class UserNotFoundByIdException extends RuntimeException{
    public UserNotFoundByIdException(String message) {
        super("User not found by ID: " + message);
    }
}
