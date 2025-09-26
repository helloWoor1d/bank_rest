package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.dto.user.UserRegisterRequest;
import com.example.bankcards.dto.user.UserUpdateRequest;
import com.example.bankcards.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "blocked", ignore = true)
    @Mapping(target = "patronymic", ignore = true)
    @Mapping(target = "lastname", ignore = true)
    @Mapping(target = "firstname", ignore = true)
    @Mapping(target = "id", ignore = true)
    User toUser(UserRegisterRequest registerRequest);

    @Mapping(target = "blocked", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "id", source = "userId")
    User toUser(UserUpdateRequest updateRequest, Long userId);

    UserDto toDto(User user);
}
