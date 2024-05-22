package com.blps.lab2.controllers;

import com.blps.lab2.dto.UserDataDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface ControllerInt {

    @Operation(summary = "Заполнение профиля")
    @PostMapping(value = "/fill_profile")
    ResponseEntity<?> fillProfile(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody UserDataDTO userDataDTO) throws Exception;
}
