package com.example.bankcards.entity.card;

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
    private Long user;
    private Long from;
    private Long to;
    private BigDecimal amount;
}
