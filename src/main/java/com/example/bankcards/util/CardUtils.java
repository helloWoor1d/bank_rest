package com.example.bankcards.util;

import lombok.experimental.UtilityClass;
import org.springframework.format.datetime.DateFormatter;

@UtilityClass
public class CardUtils {
    public static final String BANK_BIN = "8800 55";
    public static final String CARD_NUMBER_MASK = "**** **** ****";
    public static final DateFormatter DATE_FORMATTER = new DateFormatter("M/YY");
}
