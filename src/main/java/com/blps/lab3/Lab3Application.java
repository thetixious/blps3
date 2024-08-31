package com.blps.lab3;

import com.blps.lab3.dto.CreditCardDTO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Lab3Application extends CreditCardDTO {

    public static void main(String[] args) {
        SpringApplication.run(Lab3Application.class, args);
    }
}
