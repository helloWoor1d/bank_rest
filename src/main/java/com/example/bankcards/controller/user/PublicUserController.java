package com.example.bankcards.controller.user;

import com.example.bankcards.dto.mapper.UserMapper;
import com.example.bankcards.dto.user.UserDto;
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
    private final UserMapper userMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserRegisterRequest createRequest) {
        User user = userService.createUser(userMapper.toUser(createRequest));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userMapper.toDto(user));
    }
}
