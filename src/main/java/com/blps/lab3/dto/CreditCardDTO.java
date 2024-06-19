package com.blps.lab3.dto;

import com.blps.lab3.utils.Bonus;
import com.blps.lab3.utils.CardType;
import com.blps.lab3.utils.Goal;
import lombok.Data;

@Data
public class CreditCardDTO {
    private Long id;
    private String name;
    private CardType cardType;
    private Double creditLimit;
    private Goal goal;
    private Bonus bonus;

}
