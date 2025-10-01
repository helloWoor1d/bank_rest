package com.example.bankcards.controller.card;

import com.example.bankcards.dto.mapper.CardMapper;
import com.example.bankcards.dto.mapper.CardMapperImpl;
import com.example.bankcards.dto.mapper.CardRequestMapper;
import com.example.bankcards.dto.mapper.CardRequestMapperImpl;
import com.example.bankcards.dto.mapper.UserMapper;
import com.example.bankcards.dto.mapper.UserMapperImpl;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardStatus;
import com.example.bankcards.entity.user.User;
import com.example.bankcards.entity.user.UserRole;
import com.example.bankcards.service.card.CardRequestService;
import com.example.bankcards.service.card.CardService;
import com.example.bankcards.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(controllers = PrivateCardController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({CardMapperImpl.class, CardRequestMapperImpl.class,
        UserMapperImpl.class})
public class PrivateCardControllerTest {
    @MockitoBean
    private final CardService cardService;
    @MockitoBean
    private final UserService userService;
    @MockitoBean
    private final CardRequestService requestService;

    private final CardMapper cardMapper;
    private final CardRequestMapper requestMapper;
    private final UserMapper userMapper;

    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;

    private static User owner1;
    private static JwtAuthenticationToken owner1Token;
    private Card card1ByOwner1, card2ByOwner1;

    @BeforeAll
    public static void initializeUsers() {
        owner1 = User.builder()
                .id(1L)
                .email("owner1@email.com")
                .password("encodedPassword").build();

        Jwt owner1Jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", owner1.getId()).build();
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_" + UserRole.USER);
        owner1Token = new JwtAuthenticationToken(owner1Jwt, authorities);
    }

    @BeforeEach
    public void initializeCards() {
        card1ByOwner1 = Card.builder()
                .id(1L)
                .number("8800 5553 5351 1111")
                .expiryDate(LocalDate.now().plusYears(3))
                .owner(owner1)
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.valueOf(0.0)).build();
        card2ByOwner1 = Card.builder()
                .id(2L)
                .number("8800 5553 5352 2222")
                .expiryDate(LocalDate.now().plusYears(3))
                .owner(owner1)
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.valueOf(0.0)).build();
    }

    @Test
    public void shouldGetCards() throws Exception {
        Page page = new PageImpl(List.of(card1ByOwner1, card2ByOwner1),
                PageRequest.of(1, 10), 2);
        when(cardService.getUserCards(anyLong(), any(), any()))
                .thenReturn(page);

        var response = performGetAllUserCards(1L, owner1Token);
        assertThat(response.getStatus(), is(200));
    }

    public MockHttpServletResponse performGetAllUserCards(Long userId, JwtAuthenticationToken token) throws Exception {
        MvcResult result = mockMvc.perform(
                get("/api/v1/cards")
                .with(authentication(token))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andReturn();
        return result.getResponse();
    }
}
