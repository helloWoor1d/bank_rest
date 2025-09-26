package com.example.bankcards.controller.user;

import com.example.bankcards.entity.user.User;
import com.example.bankcards.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId,
                                        @AuthenticationPrincipal Jwt jwt) {
        Long adminId = Long.parseLong(jwt.getSubject());
        User user = userService.getUserByAdmin(userId, adminId);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void blockUser(@PathVariable Long userId,
                          @AuthenticationPrincipal Jwt jwt) {
        Long adminId = Long.parseLong(jwt.getSubject());
        userService.blockUser(userId, adminId);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<User> unlockUser(@PathVariable Long userId,
                                           @AuthenticationPrincipal Jwt jwt) {
        Long adminId = Long.parseLong(jwt.getSubject());
        User user = userService.unlockUser(userId, adminId);
        return ResponseEntity.ok(user);
    }
}
