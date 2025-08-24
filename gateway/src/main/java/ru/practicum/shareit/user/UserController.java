package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserCreateDto dto) {
        log.info("Adding new user: {}", dto);
        return userClient.addUser(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(
            @Valid @RequestBody UserUpdateDto dto,
            @PathVariable @Valid @NotNull Long id) {
        log.info("Updating user: {}", dto);
        return userClient.updateUser(id, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Getting all users");
        return userClient.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable @Valid @NotNull Long id) {
        log.info("Getting user by id: {}", id);
        return userClient.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable @Valid @NotNull Long id) {
        log.info("Deleting user: {}", id);
        return userClient.deleteUserById(id);
    }
}
