package com.example.bankcards.dto.user;

import com.example.bankcards.entity.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class UserDto {
    private Long id;
    private String email;
    private String firstname;
    private String lastname;
    private String patronymic;
    private UserRole role;
    private Boolean blocked;
}
