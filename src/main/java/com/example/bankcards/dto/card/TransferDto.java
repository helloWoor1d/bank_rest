package com.example.bankcards.dto.card;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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

public class TransferDto {
    @NotNull(message = "Укажите карту для перевода")
    private Long cardTo;

    @NotNull(message = "Укажите сумму перевода")
    @Min(value = 5, message = "Сумма перевода не может быть меньше 5 рублей")
    private BigDecimal amount;
}
