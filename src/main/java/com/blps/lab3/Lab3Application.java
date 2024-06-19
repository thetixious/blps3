package com.blps.lab3;

import com.blps.lab3.dto.CreditCardDTO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Lab3Application extends CreditCardDTO {

    public static void main(String[] args) {
        SpringApplication.run(Lab3Application.class, args);
    }
}
