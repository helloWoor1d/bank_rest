package com.example.bankcards.repository.card;

import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findAllByStatus(CardStatus status, Pageable pageable);
}
