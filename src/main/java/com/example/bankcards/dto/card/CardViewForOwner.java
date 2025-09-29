package com.example.bankcards.dto.card;

import com.example.bankcards.entity.card.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class CardViewForOwner {
    private int id;
    private String number;
    private String expiryDate;
    private CardStatus status;
    private BigDecimal balance;
}
