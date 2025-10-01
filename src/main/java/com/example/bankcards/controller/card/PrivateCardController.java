package com.example.bankcards.controller.card;

import com.example.bankcards.dto.card.CardViewForOwner;
import com.example.bankcards.dto.mapper.CardMapper;
import com.example.bankcards.dto.mapper.CardRequestMapper;
import com.example.bankcards.dto.request.CardRequestFullDto;
import com.example.bankcards.dto.request.CreateCardRequestDto;
import com.example.bankcards.dto.card.TransferDto;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardFilter;
import com.example.bankcards.entity.card.Transfer;
import com.example.bankcards.entity.request.CardRequest;
import com.example.bankcards.entity.user.User;
import com.example.bankcards.service.card.CardRequestService;
import com.example.bankcards.service.card.CardService;
import com.example.bankcards.service.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("api/v1/cards")
@PreAuthorize("hasRole('USER')")
public class PrivateCardController {
    private final CardService cardService;
    private final UserService userService;
    private final CardRequestService cardRequestService;

    private final CardMapper cardMapper;
    private final CardRequestMapper requestMapper;

    @GetMapping
    Page<CardViewForOwner> getCards(@AuthenticationPrincipal Jwt jwt,
                                    @RequestParam(defaultValue = "0") @Min(0) @Max(100) Integer page,
                                    @RequestParam(defaultValue = "10") @Min(0) @Max(100) Integer size,
                                    @RequestParam(defaultValue = "ALL") String filter) {
        try {
            Long userId = Long.parseLong(jwt.getSubject());
            CardFilter f = CardFilter.valueOf(filter);
            Pageable pageable = PageRequest.of(page, size);

            Page<Card> cards = cardService.getUserCards(userId, f, pageable);
            return cards.map(cardMapper::toCardViewForOwner);

        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown filter: " + filter);
        }
    }

    @PostMapping("/{cardId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CardRequestFullDto> createCardRequest(@AuthenticationPrincipal Jwt jwt,
                                                                @PathVariable Long cardId,
                                                                @Valid @RequestBody CreateCardRequestDto cardRequestDto) {
        Long requesterId = Long.parseLong(jwt.getSubject());
        User requester = userService.getUserById(requesterId);
        Card card = cardService.findCardById(cardId);
        CardRequest request = requestMapper.toCardRequest(cardRequestDto, card, requester);

        return ResponseEntity.ok(
                requestMapper.toFullDto(
                        cardRequestService.createRequest(request)));
    }

    @PostMapping("/{cardId}/transfer")
    public ResponseEntity<String> makeTransfer(@AuthenticationPrincipal Jwt jwt,
                                               @PathVariable Long cardId,
                                               @Valid @RequestBody TransferDto transferDto) {
        Transfer transfer = Transfer.builder()
                .user(userService.getUserById(Long.parseLong(jwt.getSubject())))
                .from(cardService.findCardById(cardId))
                .to(cardService.findCardById(transferDto.getCardTo()))
                .amount(transferDto.getAmount()).build();
        cardService.makeTransfer(transfer);
        return ResponseEntity.ok().body("Transfer successful!");
    }
}
