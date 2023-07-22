package ru.practicum.controller.adminController;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.service.users.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/admin/users")
public class AdminUsersController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll(@RequestParam(value = "ids", required = false) List<Long> ids,
                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                @RequestParam(defaultValue = "10") @Positive int size) {
        return userService.getAll(ids, from, size);
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid NewUserRequest dto) {
        return userService.create(dto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable @Positive Long userId) {
        userService.delete(userId);
    }
}
