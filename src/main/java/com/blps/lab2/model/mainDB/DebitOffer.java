package com.blps.lab2.model.mainDB;

import com.blps.lab2.utils.Bonus;
import com.blps.lab2.utils.Goal;
import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "debit_offer")
public class DebitOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Goal goal;

    @Enumerated(EnumType.STRING)
    private Bonus bonus;

    @Column(name = "user_id")
    private Long user_id;

    @Transient
    private User card_user;
}
