package com.example.bankcards.repository.card;

import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    @Query("SELECT c.id FROM Card AS c ")
    Page<Long> getCardId(Pageable pageable);

    @Query("SELECT c.id FROM Card AS c WHERE c.status = :status ")
    Page<Long> getCardIdByStatus(@Param("status") CardStatus status, Pageable pageable);

    @EntityGraph(attributePaths = "owner")
    List<Card> findAllByIdIn(List<Long> ids);
}
