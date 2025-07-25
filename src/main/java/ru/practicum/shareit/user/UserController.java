package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto addUser(@Valid @RequestBody UserCreateDto dto) {
        log.info("Adding new user: {}", dto);
        return userService.save(dto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto updateUser(@Valid @RequestBody UserUpdateDto dto, @PathVariable Long id) {
        log.info("Updating user: {}", dto);
        return userService.update(id, dto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getAllUsers() {
        log.info("Getting all users");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto getUserById(@PathVariable Long id) {
        log.info("Getting user by id: {}", id);
        return userService.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserById(@PathVariable Long id) {
        log.info("Deleting user: {}", id);
        userService.deleteById(id);
    }
}
