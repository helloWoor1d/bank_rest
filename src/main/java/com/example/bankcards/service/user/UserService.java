package com.example.bankcards.service.user;

import com.example.bankcards.entity.user.User;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.info("Create user with id {}", user.getId());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found"));
    }

    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        log.debug("Get user {}", userId);
        User user = getUserById(userId);
        if (user.getBlocked())
            throw new AccessDeniedException("User is blocked");
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserByAdmin(Long userId, Long adminId) {
        log.debug("Get user {} by admin {}", userId, adminId);
        return getUserById(userId);
    }

    public void deleteUser(Long userId) {
        log.info("Delete user {}", userId);
        userRepository.deleteById(userId);
    }

    public User updateUser(User user) {
        User saved = getUserById(user.getId());
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            saved.setPassword(passwordEncoder.encode(user.getPassword()));
        } if (user.getEmail() != null && !user.getEmail().isBlank()) {
            saved.setEmail(user.getEmail());
        } if (user.getFirstname() != null && !user.getFirstname().isBlank()) {
            saved.setFirstname(user.getFirstname());
        } if (user.getLastname() != null && !user.getLastname().isBlank()) {
            saved.setLastname(user.getLastname());
        } if (user.getPatronymic() != null && !user.getPatronymic().isBlank()) {
            saved.setPatronymic(user.getPatronymic());
        }
        log.debug("Update user {}", user.getId());
        return userRepository.save(saved);
    }

    public User blockUser(Long userId, Long blockerId) {
        if (userId.equals(blockerId)) {
            throw new AccessDeniedException("User cannot block itself");
        }
        User user = getUserById(userId);
        if (user.getBlocked()) throw new AccessDeniedException("User is already blocked");

        log.info("Block user with id {} by admin {}", userId, blockerId);
        user.setBlocked(true);
        return userRepository.save(user);
    }

    public User unlockUser(Long userId, Long blockerId) {
        if (userId.equals(blockerId)) {
            throw new AccessDeniedException("User cannot unlock itself");
        }
        User user = getUserById(userId);
        if (!user.getBlocked()) throw new AccessDeniedException("User is already unlocked");

        log.info("Unblock user with id {} by admin {}", userId, blockerId);
        user.setBlocked(false);
        return userRepository.save(user);
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }
}
