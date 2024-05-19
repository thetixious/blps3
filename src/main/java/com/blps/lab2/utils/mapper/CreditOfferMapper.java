package com.blps.lab2.utils.mapper;

import com.blps.lab2.dto.CreditOfferDTO;
import com.blps.lab2.model.CreditOffer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CreditOfferMapper {
    @Mapping(source = "card_user.email", target = "email")
    @Mapping(source = "card_user.passport", target = "passport")
    @Mapping(source = "card_user.salary", target = "salary")
    @Mapping(source = "credit_limit", target = "creditLimit")
    @Mapping(source = "approved", target = "approved")
    @Mapping(source = "ready", target = "ready")
    @Mapping(source = "goal", target = "goal")
    @Mapping(source = "bonus", target = "bonus")
    @Mapping(source = "cards", target = "cards")
    CreditOfferDTO toDTO(CreditOffer creditOffer);

    @Mapping(source = "goal", target = "goal")
    @Mapping(source = "bonus", target = "bonus")
    @Mapping(source = "creditLimit", target = "credit_limit")
    @Mapping(target = "card_user.email", ignore = true)
    @Mapping(target = "card_user.passport", ignore = true)
    @Mapping(target = "card_user.salary", ignore = true)
    @Mapping(target = "approved", ignore = true)
    @Mapping(target = "ready", ignore = true)
    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "id", ignore = true)
    CreditOffer toEntity(CreditOfferDTO creditOfferDTO);

}
