package com.blps.lab3.exception;

import com.blps.lab3.dto.Response;
import com.blps.lab3.exception.customException.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception ex) {
        Response response = new Response("Internal Server Error: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(NotFilledProfileException.class)
    public ResponseEntity<Response> handleNotFilledProfileException(NotFilledProfileException exception){
        Response response = new Response(exception.getMessage());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @ExceptionHandler(UserNotFoundByIdException.class)
    public ResponseEntity<Response> handleNotFoundUserException(UserNotFoundByIdException exception){
        Response response = new Response(exception.getMessage());
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OfferAlreadyExistException.class)
    public ResponseEntity<Response> handleOfferAlreadyExistException(OfferAlreadyExistException exception){
        Response response = new Response(exception.getMessage());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @ExceptionHandler(OfferNotFoundException.class)
    public ResponseEntity<Response> handleOfferNotFoundException(OfferNotFoundException exception){
        Response response = new Response(exception.getMessage());
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(NoCardWasApproved.class)
    public ResponseEntity<Response> handleNoCardWasApproved(NoCardWasApproved exception){
        Response response   = new Response(exception.getMessage());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
