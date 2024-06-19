package com.blps.lab3.model.mainDB;

import com.blps.lab3.utils.Bonus;
import com.blps.lab3.utils.CardType;
import com.blps.lab3.utils.Goal;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cards")
public class Cards {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private CardType type;
    private Double credit_limit;
    private String name;
    @Enumerated(EnumType.STRING)
    private Goal goal;
    @Enumerated(EnumType.STRING)
    private Bonus bonus;




    public Cards() {
    }


}
