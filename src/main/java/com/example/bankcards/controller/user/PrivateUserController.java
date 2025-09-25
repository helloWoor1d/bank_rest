package com.example.bankcards.controller.user;

import com.example.bankcards.dto.user.UserUpdateRequest;
import com.example.bankcards.entity.user.User;
import com.example.bankcards.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/my")
@RequiredArgsConstructor
public class PrivateUserController {
    private final UserService userService;
    
    @GetMapping
    public ResponseEntity<User> getUser(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PostMapping
    public ResponseEntity<User> updateUser(@AuthenticationPrincipal Jwt jwt,
                                           @RequestBody UserUpdateRequest updateRequest) {
        Long userId = Long.parseLong(jwt.getSubject());
        User user = User.builder()
                .id(userId)
                .email(updateRequest.getEmail())
                .password(updateRequest.getPassword())
                .firstname(updateRequest.getFirstname())
                .lastname(updateRequest.getLastname())
                .patronymic(updateRequest.getPatronymic())
                .build();
        User resp = userService.updateUser(user);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        userService.deleteUser(userId);
    }
}
