package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.request.CardRequestFullDto;
import com.example.bankcards.dto.request.CreateCardRequestDto;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.request.CardRequest;
import com.example.bankcards.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {CardMapper.class, UserMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardRequestMapper {

    @Mapping(target = "card", source = "card")
    @Mapping(target = "requester", source = "requester")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    CardRequest toCardRequest(CreateCardRequestDto cardRequest, Card card, User requester);

    CardRequestFullDto toFullDto(CardRequest cardRequest);
}
