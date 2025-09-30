package com.example.bankcards.service.user;

import com.example.bankcards.entity.user.User;
import com.example.bankcards.exception.BadOperationException;
import com.example.bankcards.exception.UserBlockedException;
import com.example.bankcards.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User u1, u2;

    @BeforeEach
    void setUp() {
        u1 = User.builder()
                .id(1L)
                .email("test1@example.com")
                .password("encodedPassword").build();
        u2 = User.builder()
                .id(2L)
                .email("test2@example.com")
                .password("encodedPassword").build();
    }

    @Test
    public void shouldEncodePasswordBeforeSaveUser() {
        User user = User.builder()
                .email("test2@example.com")
                .password("password").build();

        Mockito
                .when(passwordEncoder.encode("password"))
                .thenReturn("encodedPassword");
        Mockito.when(userRepository.save(any())).thenReturn(u2);
        userService.createUser(user);

        Mockito.verify(passwordEncoder, Mockito.times(1))
                .encode("password");
        Mockito.verify(userRepository, Mockito.times(1))
                .save(argThat(u -> "encodedPassword".equals(u.getPassword())));
    }

    @Test
    public void whenUserNotFound_thenThrowException() {
        Mockito
                .when(userRepository.findUserByEmail(anyString()))
                .thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> userService.getUserByEmail("test@test.com"));
        assertThat(ex.getMessage(), is("User with email test@test.com not found"));

        ex = assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));
        assertThat(ex.getMessage(), is("User with id 1 not found"));

        ex = assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L, 2L));
        assertThat(ex.getMessage(), is("User with id 1 not found"));
    }

    @Test
    public void whenUserFound_thenGetSuccess() {
        Mockito
                .when(userRepository.findUserByEmail("test1@example.com"))
                .thenReturn(Optional.of(u1));
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(u1));

        userService.getUserById(1L);
        userService.getUserById(1L, 2L);
        userService.getUserByEmail("test1@example.com");

        Mockito.verify(userRepository, Mockito.times(2)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).findUserByEmail("test1@example.com");
    }

    @Test
    public void whenUserBlocked_thenUpdateThrowException() {
        u1.setBlocked(true);
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(u1));
        UserBlockedException ex = assertThrows(UserBlockedException.class, () -> userService.updateUser(User.builder().id(1L).build()));
        assertThat(ex.getMessage(), is("В настоящее время операция недоступна, дождитесь разблокировки аккаунта."));
    }

    @Test
    public void whenUserNotBlocked_thenUpdateSuccess() {
        User updated = User.builder().id(1L).firstname("updated").lastname("updated").build();
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(u1));
        userService.updateUser(updated);
        Mockito
                .verify(userRepository, Mockito.times(1))
                .findById(1L);
        Mockito
                .verify(userRepository, Mockito.times(1))
                .save(argThat(u ->
                        u1.getId().equals(u.getId()) &&
                        u1.getEmail().equals(u.getEmail()) &&
                        u1.getPassword().equals(u.getPassword()) &&
                        "updated".equals(u.getFirstname()) &&
                        "updated".equals(u.getLastname())));
    }

    @Test
    public void whenUserBlocked_thenDeleteThrowException() {
        u1.setBlocked(true);
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(u1));
        UserBlockedException ex = assertThrows(UserBlockedException.class, () -> userService.deleteUser(1L));
        assertThat(ex.getMessage(), is("В настоящее время вы не можете удалить свой аккаунт, дождитесь разблокировки."));
    }

    @Test
    public void whenUserNotBlocked_thenDeleteSuccess() {
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(u1));
        userService.deleteUser(1L);

        Mockito
                .verify(userRepository, Mockito.times(1))
                .deleteById(1L);
    }

    @Test
    public void whenUserAlreadyBlocked_thenThrowException() {
        BadOperationException ex = assertThrows(BadOperationException.class, () -> userService.blockUser(1L, 1L));
        assertThat(ex.getMessage(), is("User cannot block itself"));

        u1.setBlocked(true);
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(u1));
        ex = assertThrows(BadOperationException.class, () -> userService.blockUser(1L, 2L));
        assertThat(ex.getMessage(), is("User is already blocked"));
    }

    @Test
    public void whenBlockUser_thenSuccess() {
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(u1));

        userService.blockUser(1L, 2L);
        Mockito
                .verify(userRepository, Mockito.times(1))
                .save(argThat(u -> Boolean.TRUE.equals(u.getBlocked())));
    }

    @Test
    public void whenUserAlreadyUnlocked_thenThrowException() {
        BadOperationException ex = assertThrows(BadOperationException.class, () -> userService.unlockUser(1L, 1L));
        assertThat(ex.getMessage(), is("User cannot unlock itself"));

        u1.setBlocked(false);
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(u1));
        ex = assertThrows(BadOperationException.class, () -> userService.unlockUser(1L, 2L));
        assertThat(ex.getMessage(), is("User is already unlocked"));
    }

    @Test
    public void whenUnlockUser_thenSuccess() {
        u1.setBlocked(true);
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(u1));

        userService.unlockUser(1L, 2L);
        Mockito
                .verify(userRepository, Mockito.times(1))
                .save(argThat(u -> Boolean.FALSE.equals(u.getBlocked())));
    }
}
