package com.blps.lab3.exception.customException;

public class OfferNotFoundException  extends RuntimeException{
    public OfferNotFoundException() {
        super("Offer not found");
    }
}
