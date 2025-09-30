package com.example.bankcards.controller.user;

import com.example.bankcards.config.security.ResourceServerConfig;
import com.example.bankcards.dto.mapper.UserMapper;
import com.example.bankcards.dto.mapper.UserMapperImpl;
import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.dto.user.UserUpdateRequest;
import com.example.bankcards.entity.user.User;
import com.example.bankcards.entity.user.UserRole;
import com.example.bankcards.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@WebMvcTest(PrivateUserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({ResourceServerConfig.class, UserMapperImpl.class})
public class PrivateUserControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    private final UserMapper userMapper;

    @MockitoBean
    private UserService userService;

    private static JwtAuthenticationToken adminToken;
    private static JwtAuthenticationToken userToken;

    private User u1, u2;

    @BeforeEach
    public void initializeUsers() {
        u1 = User.builder()
                .id(1L)
                .email("test1@test.com")
                .password("encodedPassword").build();
        u2 = User.builder()
                .id(2L)
                .email("test2@test.com")
                .password("encodedPassword").build();
    }

    @BeforeAll
    public static void setUp() {
        Jwt userJwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "1").build();
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_" + UserRole.USER);
        userToken = new JwtAuthenticationToken(userJwt, authorities);

        Jwt adminJwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "2").build();
        authorities = AuthorityUtils.createAuthorityList("ROLE_" + UserRole.ADMIN);
        adminToken = new JwtAuthenticationToken(adminJwt, authorities);
    }

    @Test
    public void shouldGetUser() throws Exception {
        u2.setRole(UserRole.ADMIN);
        when(userService.getUserById(1L)).thenReturn(u1);
        when(userService.getUserById(2L)).thenReturn(u2);
        var respForGetUser = performGetUser(userToken);
        var respForGetAdmin = performGetUser(adminToken);

        UserDto userDto = objectMapper.readValue(respForGetUser.getContentAsString(), UserDto.class);
        UserDto adminDto = objectMapper.readValue(respForGetAdmin.getContentAsString(), UserDto.class);

        assertThat(respForGetUser.getStatus(), is(200));
        assertThat(userDto.getId(), is(1L));
        assertThat(respForGetAdmin.getStatus(), is(200));
        assertThat(adminDto.getId(), is(2L));
    }

    public MockHttpServletResponse performGetUser(JwtAuthenticationToken token) throws Exception {
        MvcResult result = mockMvc.perform(
                get("/api/v1/users")
                        .with(authentication(token))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)).andReturn();
        return result.getResponse();
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        u1.setFirstname("Updated");
        u1.setLastname("Updated");
        when(userService.updateUser(any())).thenReturn(u1);

        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .firstname("Updated")
                .lastname("Updated").build();

        MockHttpServletResponse result = mockMvc.perform(
                patch("/api/v1/users")
                        .with(authentication(userToken))
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(result.getStatus(), is(200));
        UserDto userDto = objectMapper.readValue(result.getContentAsString(), UserDto.class);
        assertThat(userDto.getId(), is(u1.getId()));
        assertThat(userDto.getFirstname(), is("Updated"));
        assertThat(userDto.getLastname(), is("Updated"));
    }

    @Test
    public void shouldDeleteUser() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                delete("/api/v1/users")
                .with(authentication(userToken)))
                .andReturn().getResponse();
        assertThat(response.getStatus(), is(204));
    }
}
