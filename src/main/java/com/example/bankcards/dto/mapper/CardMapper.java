package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.card.CardView;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.service.user.UserService;
import com.example.bankcards.util.CardUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring",
        uses = {UserService.class,
                UserMapper.class})
public interface CardMapper {
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "expiryDate", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "id", ignore = true)

    @Mapping(target = "owner", source = "createReq.ownerId", qualifiedByName = "getUserById")
    Card toCard(CreateCardRequest createReq);

    @Mapping(target = "expiryDate",
            expression = "java(card.getExpiryDate().format(com.example.bankcards.util.CardUtils.DATE_FORMATTER))")
    @Mapping(target = "number", qualifiedByName = "maskCardNumber")
    CardView toCardView(Card card);

    @Named("maskCardNumber")
    default String maskCardNumber(String number) {          // последние 4 цифры номера добавляю к маске
        number = number.substring(15, 19);
        return CardUtils.CARD_NUMBER_MASK + number;
    }
}
