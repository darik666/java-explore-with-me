package ru.practicum.service.users;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        return userRepository.findByIdIn(ids, pageable).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto create(NewUserRequest newUser) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUserFromNewDto(newUser)));
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
