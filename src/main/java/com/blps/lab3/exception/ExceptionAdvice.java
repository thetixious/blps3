package com.blps.lab3.exception;

import com.blps.lab3.dto.Response;
import com.blps.lab3.exception.customException.NotFilledProfileException;
import com.blps.lab3.exception.customException.UserNotFoundByIdException;
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

}
