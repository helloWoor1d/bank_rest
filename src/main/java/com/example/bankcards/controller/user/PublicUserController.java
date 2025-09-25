package com.example.bankcards.controller.user;

import com.example.bankcards.dto.user.UserRegisterRequest;
import com.example.bankcards.entity.user.User;
import com.example.bankcards.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class PublicUserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> createUser(@Valid @RequestBody UserRegisterRequest createRequest) {
        return ResponseEntity.ok(userService.createUser(
                User.builder()
                        .email(createRequest.getEmail())
                        .password(createRequest.getPassword())
                        .role(createRequest.getRole()).build()));
    }
}
