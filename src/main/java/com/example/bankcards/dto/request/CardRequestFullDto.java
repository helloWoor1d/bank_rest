package com.example.bankcards.dto.request;

import com.example.bankcards.dto.card.CardViewForAdmin;
import com.example.bankcards.dto.user.UserForCard;
import com.example.bankcards.entity.request.CardRequestStatus;
import com.example.bankcards.entity.request.CardRequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class CardRequestFullDto {
    private Long id;

    private UserForCard requester;

    private CardViewForAdmin card;

    private CardRequestType requestType;

    private CardRequestStatus status;

    private LocalDateTime requestDate;
}
