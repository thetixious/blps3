package com.blps.lab2;

import com.blps.lab2.dto.CreditCardDTO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Lab2Application extends CreditCardDTO {

    public static void main(String[] args) {
        SpringApplication.run(Lab2Application.class, args);
    }
}
