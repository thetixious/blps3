package com.blps.lab2.utils.mapper;

import com.blps.lab2.dto.DebitOfferDTO;
import com.blps.lab2.model.mainDB.DebitOffer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface DebitOfferMapper {
    @Mapping(source = "goal", target = "goal")
    @Mapping(source = "bonus", target = "bonus")
    @Mapping(source = "id", target = "id")
    DebitOfferDTO toDTO(DebitOffer debitOffer);

    @Mapping(source = "goal", target = "goal")
    @Mapping(source = "bonus", target = "bonus")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "card_user", ignore = true)
    DebitOffer toEntity(DebitOfferDTO debitOfferDTO);
}
