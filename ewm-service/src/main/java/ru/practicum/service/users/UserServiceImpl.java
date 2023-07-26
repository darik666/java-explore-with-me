package ru.practicum.service.users;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * Получение информации о пользователях
     */
    @Override
    public List<UserDto> getAll(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        Page<User> userPage;

        if (ids != null && !ids.isEmpty()) {
            userPage = userRepository.findByIdIn(ids, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        return userPage.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    /**
     * Добавление нового пользователя
     */
    @Transactional
    @Override
    public UserDto create(NewUserRequest newUser) {
        if (userRepository.findByName(newUser.getName()) != null) {
            throw new AlreadyExistsException("Пользователь с таким именем уже существует");
        }
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUserFromNewDto(newUser)));
    }

    /**
     * Удаление пользователя
     */
    @Transactional
    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}