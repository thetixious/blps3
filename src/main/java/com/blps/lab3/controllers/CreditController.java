package com.blps.lab3.controllers;

import com.blps.lab3.dto.CreditOfferDTO;
import com.blps.lab3.dto.UserDataDTO;
import com.blps.lab3.service.controllerService.CommonService;
import com.blps.lab3.service.controllerService.CreditService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/credit")
public class CreditController implements ControllerInt{
    private final CreditService creditService;
    private final CommonService commonService;

    public CreditController(CreditService creditService, CommonService commonService) {

        this.creditService = creditService;
        this.commonService = commonService;
    }
    @Operation(summary = "Создание заявки на кредитную карту")
    @PostMapping(value = "/offer")
    public ResponseEntity<?> offer(@RequestHeader("Authorization") String authorizationHeader,@Valid @RequestBody CreditOfferDTO creditOfferDTO) {

        return creditService.creatRowOffer(commonService.extractIdFromJWT(authorizationHeader), creditOfferDTO);

    }
    @Operation(summary = "Вывод одобренных карт")
    @GetMapping(value = "/approved_cards")
    public ResponseEntity<?> approvedCards(@RequestHeader("Authorization") String authorizationHeader) {

        return creditService.getApprovedCards(commonService.extractIdFromJWT(authorizationHeader));

    }
    @Override
    @Operation(summary = "Заполнение профиля")
    @PostMapping(value = "/fill_profile")
    public ResponseEntity<?> fillProfile(@RequestHeader("Authorization") String authorizationHeader,@Valid @RequestBody UserDataDTO userDataDTO) {
        return commonService.toFillProfile(commonService.extractIdFromJWT(authorizationHeader), userDataDTO);
    }
    @Operation(summary = "Вывод подходящих карт по бонусной программе")
    @GetMapping(value="/get_cards")
    public ResponseEntity<?> getCards(@RequestHeader("Authorization") String authorizationHeader){
        return creditService.getYetUnapprovedCards(commonService.extractIdFromJWT(authorizationHeader));
    }
    @Operation(summary = "Выбор предпочитаемых карт")
    @PostMapping(value="/choose_cards")
    public ResponseEntity<?> chooseCards(@RequestHeader("Authorization") String authorizationHeader,@RequestBody List<Long> cardsId){
        return creditService.updateOfferByChosenCards(commonService.extractIdFromJWT(authorizationHeader),cardsId);



    }
}
