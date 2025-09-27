package com.example.bankcards.service.card;

import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardFilter;
import com.example.bankcards.entity.card.CardStatus;
import com.example.bankcards.repository.card.CardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class CardService {
    private final CardRepository cardRepository;

    public Card createCard(Card card, Long adminId) {
        card.setNumber(NumberGenerator.generateNumber());   // toDo: подумать над повторной попыткой генерации номера в случае исключения
        card.setExpiryDate(LocalDate.now().plusYears(3));
        card.setBalance(BigDecimal.valueOf(0.0));
        card.setStatus(CardStatus.ACTIVE);

        Card saved = cardRepository.save(card);
        log.info("Create card {} by {}", saved.getId(), adminId);
        return saved;
    }

    public void deleteCard(Long cardId, Long adminId) {
        log.info("Delete card {} by {}", cardId, adminId);
        cardRepository.deleteById(cardId);
    }

    public Card activateCard(Long cardId, Boolean isActive, Long adminId) {
        log.info("Card {} activate ({}) by {}", cardId, isActive, adminId);
        Card card = findCardById(cardId);
        if (isActive) {
            card.setStatus(CardStatus.ACTIVE);
        } else {
            card.setStatus(CardStatus.BLOCKED);
        }
        return cardRepository.save(card);
    }

    @Transactional(readOnly = true)
    public Page<Card> getCardsByStatus(Long adminId, CardFilter filter, Pageable pageable) {
        if (filter.equals(CardFilter.ALL)) {
            log.debug("Get ALL cards by {}", adminId);
            return cardRepository.findAll(pageable);
        } else {
            log.debug("Get {} cards by {}", filter, adminId);
            return cardRepository.findAllByStatus(CardStatus.valueOf(filter.toString()), pageable);
        }
    }

    private Card findCardById(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card with id " + cardId + " not found"));
    }
}
