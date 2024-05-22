package com.blps.lab2.security.utils;

import lombok.Data;

@Data
public class SignInRequest {

    private String username;
    private String password;
}
