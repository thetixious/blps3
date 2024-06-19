package com.blps.lab3.utils.mapper;

import com.blps.lab3.dto.DebitCardDTO;
import com.blps.lab3.model.mainDB.Cards;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DebitCardMapper {
    @Mapping(source = "name", target = "name")
    @Mapping(source = "goal", target = "goal")
    @Mapping(source = "type", target = "cardType")
    @Mapping(source = "bonus", target = "bonus")
    DebitCardDTO toDTO(Cards card);






}
