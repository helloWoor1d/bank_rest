package com.example.bankcards.entity.request;

import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "card_requests")
public class CardRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_id_gen")
    @SequenceGenerator(name = "seq_id_gen", sequenceName = "card_requests_id_seq", allocationSize = 5)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type")
    private CardRequestType requestType;

    @Enumerated(EnumType.STRING)
    private CardRequestStatus status;

    @Column(name = "request_date")
    private LocalDateTime requestDate;
}
