package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.entity.card.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardMapper {
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "expiryDate", ignore = true)
    @Mapping(target = "balance", ignore = true)
    Card toCard(CreateCardRequest createCardRequest);
}
