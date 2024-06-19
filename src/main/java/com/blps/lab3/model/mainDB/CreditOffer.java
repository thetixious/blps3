package com.blps.lab3.model.mainDB;
import com.blps.lab3.utils.Bonus;
import com.blps.lab3.utils.Goal;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "credit_offer")
public class CreditOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long user_id;

    @Transient
    private User card_user;

    @Enumerated(EnumType.STRING)
    private Goal goal;

    @Enumerated(EnumType.STRING)
    private Bonus bonus;

    private Double credit_limit;

    private Boolean approved;
    private Boolean ready;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "credit_offer_cards",
            joinColumns = @JoinColumn(name = "credit_offer_id"),
            inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private Set<Cards> cards = new HashSet<>();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="credit_offer_preferred_cards",
            joinColumns = @JoinColumn(name="credit_offer_id"),
            inverseJoinColumns = @JoinColumn(name="card_id")
    )
    private Set<Cards> preferredCards = new HashSet<>();


}
