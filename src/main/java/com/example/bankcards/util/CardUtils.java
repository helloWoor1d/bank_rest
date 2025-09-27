package com.example.bankcards.util;

import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class CardUtils {
    public static final String BANK_BIN = "8800 55";
    public static final String CARD_NUMBER_MASK = "**** **** **** ";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/yy");
}
