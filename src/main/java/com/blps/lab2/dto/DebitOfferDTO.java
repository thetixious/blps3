package com.blps.lab2.dto;


import com.blps.lab2.utils.Bonus;
import com.blps.lab2.utils.Goal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class DebitOfferDTO {
    @JsonIgnore
    private Long id;
    private Goal goal;
    private Bonus bonus;



}
