package com.example.bankcards.security;

import com.example.bankcards.security.model.SecurityUser;
import com.example.bankcards.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("Load user by username: {}", username);
        return new SecurityUser(
                userService.getUserByEmail(username));
    }
}
