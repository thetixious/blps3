package com.blps.lab2.security.utils;

import lombok.Data;

import java.util.List;

@Data
public class DataRequest {
    private LongWrapper longWrapper;
    private List<Long> cardsId;

}
