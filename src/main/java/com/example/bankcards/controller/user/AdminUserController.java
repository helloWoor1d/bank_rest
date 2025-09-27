package com.example.bankcards.controller.user;

import com.example.bankcards.dto.mapper.UserMapper;
import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.entity.user.User;
import com.example.bankcards.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId,
                                           @AuthenticationPrincipal Jwt jwt) {
        Long adminId = Long.parseLong(jwt.getSubject());
        User user = userService.getUserByAdmin(userId, adminId);
        return ResponseEntity.ok(
                userMapper.toDto(user));
    }

    @PatchMapping("/{userId}/block")
    public ResponseEntity<UserDto> blockUser(@PathVariable Long userId,
                                             @AuthenticationPrincipal Jwt jwt) {
        Long adminId = Long.parseLong(jwt.getSubject());
        User user = userService.blockUser(userId, adminId);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PatchMapping("/{userId}/unlock")
    public ResponseEntity<UserDto> unlockUser(@PathVariable Long userId,
                                              @AuthenticationPrincipal Jwt jwt) {
        Long adminId = Long.parseLong(jwt.getSubject());
        User user = userService.unlockUser(userId, adminId);
        return ResponseEntity.ok(
                userMapper.toDto(user));
    }
}
