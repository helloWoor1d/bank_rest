package com.example.bankcards.controller.user;

import com.example.bankcards.dto.mapper.UserMapper;
import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.dto.user.UserUpdateRequest;
import com.example.bankcards.entity.user.User;
import com.example.bankcards.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class PrivateUserController {
    private final UserService userService;
    private final UserMapper userMapper;
    
    @GetMapping
    public ResponseEntity<UserDto> getUser(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        return ResponseEntity.ok(userMapper.toDto(
                userService.getUserById(userId)));
    }

    @PatchMapping
    public ResponseEntity<UserDto> updateUser(@AuthenticationPrincipal Jwt jwt,
                                              @Valid @RequestBody UserUpdateRequest updateRequest) {
        Long userId = Long.parseLong(jwt.getSubject());
        User user = userMapper.toUser(updateRequest, userId);
        return ResponseEntity.ok(userMapper.toDto(
                userService.updateUser(user)));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        userService.deleteUser(userId);
    }
}
