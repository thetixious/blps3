package com.blps.lab3.controllers;

import com.blps.lab3.dto.DebitOfferDTO;
import com.blps.lab3.dto.UserDataDTO;
import com.blps.lab3.service.CommonService;
import com.blps.lab3.service.DebitService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/debit")
public class DebitController implements ControllerInt{

    private final DebitService debitService;
    private final CommonService commonService;

    public DebitController(DebitService debitService, CommonService commonService) {
        this.debitService = debitService;
        this.commonService = commonService;
    }

    @Operation(summary = "Вывод подходящих карт")
    @GetMapping(value = "/cards")
    public ResponseEntity<?> suitableCards(@RequestHeader("Authorization") String authorizationHeader) {

        return debitService.getCards(commonService.extractIdFromJWT(authorizationHeader));

    }
    @Operation(summary = "Создание заявки на дебетовую карту")
    @PostMapping(value = "/offer")
    public ResponseEntity<?> createOffer(@RequestHeader("Authorization") String authorizationHeader,@Valid @RequestBody DebitOfferDTO debitOfferDTO) {

        return debitService.creatOffer(commonService.extractIdFromJWT(authorizationHeader), debitOfferDTO);

    }
    @Operation(summary = "Заполнение профиля")
    @PostMapping(value = "/fill_profile")
    public ResponseEntity<?> fillProfile (@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody UserDataDTO userDataDTO) throws Exception {
        return commonService.toFillProfile(commonService.extractIdFromJWT(authorizationHeader),userDataDTO);
    }


}
