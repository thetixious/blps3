package com.blps.lab3.exception.customException;

public class NoCardWasApproved extends RuntimeException{
    public NoCardWasApproved(){
        super("No card was approved");
    }

}
