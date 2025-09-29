package com.example.bankcards.dto.card;

import com.example.bankcards.dto.user.UserForCard;
import com.example.bankcards.entity.card.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class CardViewForAdmin {
    private int id;
    private String number;
    private String expiryDate;
    private UserForCard owner;
    private CardStatus status;
}
