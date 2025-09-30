package com.example.bankcards.controller.user;

import com.example.bankcards.config.security.ResourceServerConfig;
import com.example.bankcards.dto.mapper.UserMapper;
import com.example.bankcards.dto.mapper.UserMapperImpl;
import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.entity.user.User;
import com.example.bankcards.entity.user.UserRole;
import com.example.bankcards.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@WebMvcTest(controllers = AdminUserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({ResourceServerConfig.class, UserMapperImpl.class})
public class AdminUserControllerTest {
    @MockitoBean
    private final UserService userService;

    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;

    private static JwtAuthenticationToken adminToken;
    private static JwtAuthenticationToken userToken;

    private User u1;

    @BeforeEach
    public void initializeUsers() {
        u1 = User.builder()
                .id(1L)
                .email("test1@test.com")
                .password("encodedPassword").build();
    }

    @BeforeAll
    public static void setUp() {
        Jwt userJwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", 1L).build();
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_" + UserRole.USER);
        userToken = new JwtAuthenticationToken(userJwt, authorities);

        Jwt adminJwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "2").build();
        authorities = AuthorityUtils.createAuthorityList("ROLE_" + UserRole.ADMIN);
        adminToken = new JwtAuthenticationToken(adminJwt, authorities);
    }

    @Test
    public void shouldGetUserByAdmin() throws Exception {
        when(userService.getUserById(1L, 2L))
                .thenReturn(u1);
        var result = performGetUserByAdmin(1L, adminToken);

        UserDto userDto = objectMapper.readValue(result.getContentAsString(), UserDto.class);
        assertThat(userDto, not(hasProperty("password")));
        assertThat(result.getStatus(), is(200));
    }

    public MockHttpServletResponse performGetUserByAdmin(Long userId, JwtAuthenticationToken token) throws Exception {
        MvcResult result = mockMvc.perform(
                get("/api/v1/admin/users/{userId}", userId)
                        .with(authentication(token))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andReturn();
        return result.getResponse();
    }

    @Test
    public void shouldBlockUser() throws Exception {
        u1.setBlocked(true);
        when(userService.blockUser(1L, 2L)).thenReturn(u1);

        var result = performBlockUser(u1.getId(), adminToken);
        UserDto userDto = objectMapper.readValue(result.getContentAsString(), UserDto.class);

        assertThat(userDto, not(hasProperty("password")));
        assertThat(userDto.getBlocked(), is(true));
        assertThat(result.getStatus(), is(200));
        verify(userService, Mockito.times(1)).blockUser(1L, 2L);
    }

    public MockHttpServletResponse performBlockUser(Long userId, JwtAuthenticationToken token) throws Exception {
        MvcResult result = mockMvc.perform(
                patch("/api/v1/admin/users/{userId}/block", userId)
                        .with(authentication(token))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andReturn();
        return result.getResponse();
    }

    @Test
    public void shouldUnlockUser() throws Exception {
        u1.setBlocked(false);
        when(userService.unlockUser(1L, 2L)).thenReturn(u1);

        var result = performUnlockUser(u1.getId(), adminToken);
        UserDto userDto = objectMapper.readValue(result.getContentAsString(), UserDto.class);

        assertThat(userDto, not(hasProperty("password")));
        assertThat(userDto.getBlocked(), is(false));
        assertThat(result.getStatus(), is(200));
        verify(userService, Mockito.times(1)).unlockUser(1L, 2L);
    }

    public MockHttpServletResponse performUnlockUser(Long userId, JwtAuthenticationToken token) throws Exception {
        MvcResult result = mockMvc.perform(
                patch("/api/v1/admin/users/{userId}/unlock", userId)
                                .with(authentication(token))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andReturn();
        return result.getResponse();
    }

    @Test
    public void whenUserNotAdmin_thenResponseStatusIsForbidden() throws Exception {
        var result1 = performGetUserByAdmin(u1.getId(), userToken);
        var result2 = performBlockUser(u1.getId(), userToken);
        var result3 = performUnlockUser(u1.getId(), userToken);

        assertThat(result1.getStatus(), is(403));
        assertThat(result2.getStatus(), is(403));
        assertThat(result3.getStatus(), is(403));
    }
}
