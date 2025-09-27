package com.example.bankcards.controller.card;

import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.mapper.CardMapper;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardFilter;
import com.example.bankcards.service.card.CardService;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/admin/cards")
@PreAuthorize("hasRole('ADMIN')")
public class CardAdminController {
    private final CardMapper cardMapper;
    private final CardService cardService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Card> createCard(@RequestBody CreateCardRequest createRequest,
                                           @AuthenticationPrincipal Jwt jwt) {
        Long adminId = Long.parseLong(jwt.getSubject());
        Card card = cardMapper.toCard(createRequest);
        return ResponseEntity.ok(cardService.createCard(card, adminId));
    }

    @DeleteMapping("/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable Long cardId,
                           @AuthenticationPrincipal Jwt jwt) {
        Long adminId = Long.parseLong(jwt.getSubject());
        cardService.deleteCard(cardId, adminId);
    }

    @PatchMapping("/{cardId}/activate")
    public ResponseEntity<Card> activateCard(@PathVariable Long cardId,
                                             @AuthenticationPrincipal Jwt jwt) {
        Long adminId = Long.parseLong(jwt.getSubject());
        Card card = cardService.activateCard(cardId, true, adminId);
        return ResponseEntity.ok(card);
    }

    @PatchMapping("/{cardId}/deactivate")
    public ResponseEntity<Card> deactivateCard(@PathVariable Long cardId,
                                               @AuthenticationPrincipal Jwt jwt) {
        Long adminId = Long.parseLong(jwt.getSubject());
        Card card = cardService.activateCard(cardId, false, adminId);
        return ResponseEntity.ok(card);
    }

    @GetMapping
    public Page<Card> getCards(@RequestParam(defaultValue = "ALL") String state,
                               @Min(0) @Max(100) @RequestParam(defaultValue = "0") Integer page,
                               @Min(0) @Max(100) @RequestParam(defaultValue = "10") Integer size,
                               @AuthenticationPrincipal Jwt jwt) {
        try {
            CardFilter filter = CardFilter.valueOf(state.toUpperCase());
            Long adminId = Long.parseLong(jwt.getSubject());
            Pageable pageable = PageRequest.of(page, size);

            return cardService.getCardsByStatus(adminId, filter, pageable);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Unknown state " + state);            //toDo: подумать над исключением + обработать его в handler-е
        }
    }
}
