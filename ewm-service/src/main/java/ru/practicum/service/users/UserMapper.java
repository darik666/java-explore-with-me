package ru.practicum.service.users;

import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.User;

import java.time.LocalDateTime;

/**
 * Маппер пользователей
 */
public class UserMapper {
    public static UserShortDto toUserShortDto(User user) {
        UserShortDto dto = new UserShortDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        return dto;
    }

    public static UserDto toUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public static User toUserFromNewDto(NewUserRequest newUser) {
        User user = new User();
        user.setCreated(LocalDateTime.now());
        user.setEmail(newUser.getEmail());
        user.setName(newUser.getName());
        return user;
    }
}