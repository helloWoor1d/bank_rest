package com.example.bankcards.service.card;

import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardStatus;
import com.example.bankcards.entity.card.Transfer;
import com.example.bankcards.entity.user.User;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.BadOperationException;
import com.example.bankcards.exception.UserBlockedException;
import com.example.bankcards.repository.card.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {
    @InjectMocks
    private CardService cardService;

    @Mock
    private CardRepository cardRepository;

    private User owner;
    private Card card1, card2;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .email("test1@example.com")
                .password("encodedPassword").build();
        card1 = Card.builder()
                .id(1L)
                .owner(owner).build();
        card2 = Card.builder()
                .id(2L)
                .owner(owner).build();
    }

    @Test
    public void shouldCreateCard() {
        when(cardRepository.save(any(Card.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        Card saved = cardService.createCard(card1, 100L);

        verify(cardRepository, Mockito.times(1)).save(card1);
        assertThat(saved.getNumber(), is(notNullValue()));
        assertThat(saved.getExpiryDate(), is(notNullValue()));
        assertThat(saved.getBalance(), is(BigDecimal.valueOf(0.0)));
        assertThat(saved.getStatus(), is(CardStatus.ACTIVE));
        assertThat(saved.getOwner(), is(owner));
    }

    @Test
    public void shouldActivateCard() {
        when(cardRepository.save(any(Card.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(cardRepository.findById(card1.getId()))
                .thenReturn(Optional.of(card1));

        Card activated = cardService.activateCard(card1.getId(), true, 100L);
        assertThat(activated.getStatus(), is(CardStatus.ACTIVE));

        Card blocked = cardService.activateCard(card1.getId(), false, 100L);
        assertThat(blocked.getStatus(), is(CardStatus.BLOCKED));
        verify(cardRepository, Mockito.times(2)).save(card1);
    }

    @Test
    public void whenBlockedUserMakeTransfer_thenThrowException() {
        owner.setBlocked(true);
        Transfer transfer = Transfer.builder().user(owner).build();

        UserBlockedException ex = assertThrows(UserBlockedException.class,
                () -> cardService.makeTransfer(transfer));
        assertThat(ex.getMessage(), is("Переводы в настоящее время недоступны, дождитесь разблокировки аккаунта"));
    }

    @Test
    public void whenBlockedCardMakeTransfer_thenThrowException() {
        card1.setStatus(CardStatus.BLOCKED);
        Transfer transfer = Transfer.builder()
                .user(owner)
                .from(card1).to(card2).build();

        BadOperationException ex = assertThrows(BadOperationException.class,
                () -> cardService.makeTransfer(transfer));
        assertThat(ex.getMessage(), is("Невозможно осуществить перевод, убедитесь что карты активны"));

        card1.setStatus(CardStatus.ACTIVE);
        card2.setStatus(CardStatus.BLOCKED);
        ex = assertThrows(BadOperationException.class,
                () -> cardService.makeTransfer(transfer));
        assertThat(ex.getMessage(), is("Невозможно осуществить перевод, убедитесь что карты активны"));
    }

    @Test
    public void whenNotEnoughBalanceOnCard_thenTransferThrowException() {
        card1.setStatus(CardStatus.ACTIVE);
        card2.setStatus(CardStatus.ACTIVE);
        card1.setBalance(BigDecimal.valueOf(12.99999999));
        card2.setBalance(BigDecimal.valueOf(0.0));
        Transfer transfer = Transfer.builder()
                .user(owner)
                .from(card1).to(card2)
                .amount(BigDecimal.valueOf(13)).build();

        BadOperationException ex = assertThrows(BadOperationException.class,
                () -> cardService.makeTransfer(transfer));
        assertThat(ex.getMessage(), is("Недостаточно средств для перевода"));
    }

    @Test
    public void whenUserIsNotCardOwner_thenTransferThrowException() {
        User notOwner = User.builder().id(13L).build();
        card1.setStatus(CardStatus.ACTIVE);
        card2.setStatus(CardStatus.ACTIVE);
        card1.setBalance(BigDecimal.valueOf(20));
        card2.setBalance(BigDecimal.valueOf(20));

        Transfer transfer = Transfer.builder()
                .user(notOwner)
                .from(card1).to(card2)
                .amount(BigDecimal.valueOf(13)).build();

        AccessDeniedException ex = assertThrows(AccessDeniedException.class,
                () -> cardService.makeTransfer(transfer));
        assertThat(ex.getMessage(), is("Переводы можно осуществлять только между своими картами"));
    }

    @Test
    public void shouldMakeTransfer() {
        card1.setStatus(CardStatus.ACTIVE);
        card2.setStatus(CardStatus.ACTIVE);
        card1.setBalance(BigDecimal.valueOf(20));
        card2.setBalance(BigDecimal.valueOf(20));
        Transfer transfer = Transfer.builder()
                .user(owner)
                .from(card1).to(card2)
                .amount(BigDecimal.valueOf(10)).build();

        when(cardRepository.save(any(Card.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        cardService.makeTransfer(transfer);
        assertThat(card1.getBalance().compareTo(BigDecimal.TEN), is(0));
        assertThat(card2.getBalance().compareTo(BigDecimal.valueOf(30)), is(0));
    }
}
