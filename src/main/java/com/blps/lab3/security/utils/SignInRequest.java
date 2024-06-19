package com.blps.lab3.security.utils;

import lombok.Data;

@Data
public class SignInRequest {

    private String username;
    private String password;
}
