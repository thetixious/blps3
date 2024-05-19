package com.blps.lab2.utils.mapper;

import com.blps.lab2.dto.CreditCardDTO;
import com.blps.lab2.model.Cards;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CreditCardMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "goal", target = "goal")
    @Mapping(source = "type", target = "cardType")
    @Mapping(source = "bonus", target = "bonus")
    @Mapping(source = "credit_limit", target = "creditLimit")
    CreditCardDTO toDTO(Cards card);


}
