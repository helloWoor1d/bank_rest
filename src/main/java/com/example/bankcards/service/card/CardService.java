package com.example.bankcards.service.card;

import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardFilter;
import com.example.bankcards.entity.card.CardStatus;
import com.example.bankcards.entity.card.Transfer;
import com.example.bankcards.exception.BadOperationException;
import com.example.bankcards.exception.UserBlockedException;
import com.example.bankcards.repository.card.CardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    public Page<Card> getUserCards(Long userId, CardFilter filter, Pageable pageable) {
        log.debug("User {} get his {} cards", userId, filter);
        if (filter.equals(CardFilter.ALL)) {
            return cardRepository.findAllByOwnerId(userId, pageable);
        }
        return cardRepository.findAllByOwnerIdAndStatus(userId, CardStatus.valueOf(filter.toString()), pageable);
    }

    @Transactional(readOnly = true)
    public Page<Card> getCardsByStatus(Long adminId, CardFilter filter, Pageable pageable) {
        Page<Long> cardIds;
        if (filter.equals(CardFilter.ALL)) {
            log.debug("Get ALL cards id by admin {}", adminId);
            cardIds = cardRepository.getCardId(pageable);
        } else {
            log.debug("Get {} cards id by admin {}", filter, adminId);
            cardIds = cardRepository.getCardIdByStatus(CardStatus.valueOf(filter.toString()), pageable);
        }
        log.debug("Get {} cards by admin {}", filter, adminId);
        List<Card> cards = cardRepository.findAllByIdIn(cardIds.getContent());
        return new PageImpl<>(cards, pageable, cardIds.getTotalElements());
    }

    public void makeTransfer(Transfer transfer) {
        if (transfer.getUser().getBlocked()) {
            throw new UserBlockedException("Переводы в настоящее время недоступны, дождитесь разблокировки аккаунта");
        }
        if (transfer.getFrom().getStatus() != CardStatus.ACTIVE ||
                transfer.getTo().getStatus() != CardStatus.ACTIVE) {
            throw new BadOperationException("Невозможно осуществить перевод, убедитесь что карты активны");
        }
        if (transfer.getFrom().getBalance().compareTo(transfer.getAmount()) < 0) {
            throw new BadOperationException("Недостаточно средств для перевода");
        }
        log.debug("Transfer from {} to {} by {}", transfer.getFrom(), transfer.getTo(), transfer.getUser());
        transfer.getFrom().setBalance(transfer.getFrom().getBalance().subtract(transfer.getAmount()));
        transfer.getTo().setBalance(transfer.getTo().getBalance().add(transfer.getAmount()));
        cardRepository.save(transfer.getFrom());
        cardRepository.save(transfer.getTo());
    }

    @Named("findCardById")
    public Card findCardById(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card with id " + cardId + " not found"));
    }
}
