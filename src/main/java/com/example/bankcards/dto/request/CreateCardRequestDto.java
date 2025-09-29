package com.example.bankcards.dto.request;

import com.example.bankcards.entity.request.CardRequestType;
import jakarta.validation.constraints.NotNull;
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

public class CreateCardRequestDto {
    @NotNull(message = "Укажите тип запроса")
    private CardRequestType requestType;
}
