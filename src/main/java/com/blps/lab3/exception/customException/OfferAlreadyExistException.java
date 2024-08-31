package com.blps.lab3.exception.customException;


public class OfferAlreadyExistException extends RuntimeException {
    public OfferAlreadyExistException() {
        super("Offer already exist");
    }
}
