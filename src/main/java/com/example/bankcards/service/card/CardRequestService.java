package com.example.bankcards.service.card;

import com.example.bankcards.entity.request.CardRequest;
import com.example.bankcards.entity.request.CardRequestStatus;
import com.example.bankcards.exception.BadOperationException;
import com.example.bankcards.exception.UserBlockedException;
import com.example.bankcards.repository.card.CardRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CardRequestService {
    private final CardRequestRepository cardRequestRepository;

    public CardRequest createRequest(CardRequest cardRequest) {
        if (!cardRequest.getCard().getOwner().getId().equals(cardRequest.getRequester().getId())) {
            throw new BadOperationException("Нельзя оставить запрос на карту, не принадлежащую вам");
        }
        if (cardRequest.getRequester().getBlocked()) {
            throw new UserBlockedException("В настоящее время операция недоступна, дождитесь разблокировки аккаунта.");
        }
        cardRequest.setRequestDate(LocalDateTime.now());
        cardRequest.setStatus(CardRequestStatus.IN_PROGRESS);
        log.info("Create request {} with id {} by {}", cardRequest.getRequestType(), cardRequest.getCard().getId(), cardRequest.getRequester().getId());
        return cardRequestRepository.save(cardRequest);
    }
}
