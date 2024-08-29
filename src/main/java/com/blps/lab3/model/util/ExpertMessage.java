package com.blps.lab3.model.util;

import com.blps.lab3.model.mainDB.Cards;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Data
@Builder
public class ExpertMessage implements Serializable {
    private Long creditOfferId;
    private Long userId;
    private String candidateName;
    private String candidateSurname;
    private String candidatePassport;
    private Double candidateCreditLimit;
    private Double candidateSalary;
    private Set<Cards> preferredCards;


}
