package ru.practicum.service.users;

import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll(List<Long> ids, int from, int size);

    UserDto create(NewUserRequest newUser);

    void delete(Long userId);
}
