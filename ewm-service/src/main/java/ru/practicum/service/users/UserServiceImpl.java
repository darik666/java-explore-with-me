package ru.practicum.service.users;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        log.debug("Получение списка пользователей: ", userPage.getContent());
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
            log.warn("Пользователь с именем {} уже существует", newUser.getName());
            throw new AlreadyExistsException("Пользователь с именем " + newUser.getName() + "уже существует");
        }
        log.debug("Создание пользователя: ", newUser);
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUserFromNewDto(newUser)));
    }

    /**
     * Удаление пользователя
     */
    @Transactional
    @Override
    public void delete(Long userId) {
        log.debug("Удаление пользователя id=", userId);
        userRepository.deleteById(userId);
    }
}