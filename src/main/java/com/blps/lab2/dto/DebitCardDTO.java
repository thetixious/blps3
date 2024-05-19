package com.blps.lab2.dto;

import com.blps.lab2.utils.Bonus;
import com.blps.lab2.utils.CardType;
import com.blps.lab2.utils.Goal;
import lombok.Data;

@Data
public class DebitCardDTO {
    private String name;
    private CardType cardType;
    private Goal goal;
    private Bonus bonus;

}
