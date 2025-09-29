package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.card.CardViewForAdmin;
import com.example.bankcards.dto.card.CardViewForOwner;
import com.example.bankcards.dto.card.CreateCardDto;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.service.user.UserService;
import com.example.bankcards.util.CardUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {UserService.class,
                UserMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardMapper {
    @Mapping(target = "owner", source = "createReq.ownerId", qualifiedByName = "getUserReference")
    Card toCard(CreateCardDto createReq);

    @Mapping(target = "expiryDate",
            expression = "java(card.getExpiryDate().format(com.example.bankcards.util.CardUtils.DATE_FORMATTER))")
    @Mapping(target = "number", qualifiedByName = "maskCardNumber")
    CardViewForAdmin toCardViewForAdmin(Card card);

    @Mapping(target = "expiryDate",
            expression = "java(card.getExpiryDate().format(com.example.bankcards.util.CardUtils.DATE_FORMATTER))")
    @Mapping(target = "number", qualifiedByName = "maskCardNumber")
    CardViewForOwner toCardViewForOwner(Card card);

    @Named("maskCardNumber")
    default String maskCardNumber(String number) {          // последние 4 цифры номера добавляю к маске
        number = number.substring(15, 19);
        return CardUtils.CARD_NUMBER_MASK + number;
    }
}
