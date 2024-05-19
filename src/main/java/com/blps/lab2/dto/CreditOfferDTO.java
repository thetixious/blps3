package com.blps.lab2.dto;

import com.blps.lab2.model.Cards;
import com.blps.lab2.utils.Bonus;
import com.blps.lab2.utils.Goal;
import lombok.Data;

import java.util.List;

@Data
public class CreditOfferDTO {
    private String email;
    private String passport;
    private Double salary;
    private Boolean approved;
    private Boolean ready;

    private Goal goal;
    private Bonus bonus;
    private Double creditLimit;
    private List<Cards> cards;
}
