package com.example.bankcards.controller.user;

import com.example.bankcards.config.security.ResourceServerConfig;
import com.example.bankcards.dto.mapper.UserMapper;
import com.example.bankcards.dto.user.UserRegisterRequest;
import com.example.bankcards.entity.user.User;
import com.example.bankcards.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = PublicUserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(ResourceServerConfig.class)
public class PublicUserControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    private User u1;

    @BeforeEach
    void setUp() {
        u1 = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("encodedPassword").build();
    }

    @Test
    public void shouldCreateUser() throws Exception {
        when(userService.createUser(any())).thenReturn(u1);
        UserRegisterRequest request = UserRegisterRequest.builder()
                .email("test@test.com")
                .password("password").build();

        MvcResult result = mockMvc.
                perform(post("/api/v1/users")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andReturn();

        assertThat(result.getResponse().getStatus(), is(201));
        verify(userService, Mockito.times(1)).createUser(any());
    }
}
