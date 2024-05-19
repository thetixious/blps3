package com.blps.lab2.dto.utils;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SignUpRequest {
    private String username;
    private String email;
    private String password;

}
