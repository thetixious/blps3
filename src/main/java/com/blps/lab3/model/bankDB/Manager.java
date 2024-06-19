package com.blps.lab3.model.bankDB;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "manager")
public class Manager {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String data;
    private Boolean status;

}
