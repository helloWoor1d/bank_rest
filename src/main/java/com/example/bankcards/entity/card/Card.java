package com.example.bankcards.entity.card;

import lombok.Builder;
import com.example.bankcards.entity.user.User;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder

public class Card {
    private String number;
    private LocalDate expiryDate;
    private User owner;
    private CardStatus status;
    private BigDecimal balance;
}
