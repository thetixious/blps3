package com.blps.lab3.exception.customException;



public class NotFilledProfileException extends RuntimeException{
    public NotFilledProfileException(){
        super("Fill out your profile first ");
    }
}
