package com.example.bankcards.entity.card;

import com.example.bankcards.entity.user.User;
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

public class Transfer {
    private User user;
    private Card from;
    private Card to;
    private BigDecimal amount;
}
