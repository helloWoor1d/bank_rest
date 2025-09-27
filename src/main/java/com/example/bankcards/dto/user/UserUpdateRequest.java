package com.example.bankcards.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class UserUpdateRequest {        //toDo: подумать над валидацией
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private String patronymic;
}
