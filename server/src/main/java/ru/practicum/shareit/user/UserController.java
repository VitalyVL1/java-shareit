package ru.practicum.shareit.user;

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
 * REST-контроллер для управления пользователями в модуле server.
 * <p>
 * Предоставляет endpoints для создания, обновления, получения и удаления пользователей.
 * Все запросы уже прошли первичную валидацию в gateway, поэтому здесь валидация минимальна.
 * </p>
 *
 * @see UserService
 * @see UserCreateDto
 * @see UserUpdateDto
 * @see UserResponseDto
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    /**
     * Создает нового пользователя.
     * <p>
     * HTTP метод: POST /users
     * </p>
     *
     * @param dto DTO с данными для создания пользователя (имя и email)
     * @return созданный пользователь в виде DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto addUser(@RequestBody UserCreateDto dto) {
        log.info("Adding new user: {}", dto);
        return userService.save(dto);
    }

    /**
     * Обновляет существующего пользователя.
     * <p>
     * HTTP метод: PATCH /users/{id}
     * </p>
     *
     * @param dto DTO с обновляемыми полями (оба поля опциональны)
     * @param id  идентификатор обновляемого пользователя (из пути запроса)
     * @return обновленный пользователь в виде DTO
     */
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto updateUser(@RequestBody UserUpdateDto dto, @PathVariable Long id) {
        log.info("Updating user: {}", dto);
        return userService.update(id, dto);
    }

    /**
     * Получает список всех пользователей.
     * <p>
     * HTTP метод: GET /users
     * </p>
     *
     * @return список всех пользователей в виде DTO
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getAllUsers() {
        log.info("Getting all users");
        return userService.findAll();
    }

    /**
     * Получает информацию о конкретном пользователе по его идентификатору.
     * <p>
     * HTTP метод: GET /users/{id}
     * </p>
     *
     * @param id идентификатор пользователя (из пути запроса)
     * @return данные пользователя в виде DTO
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto getUserById(@PathVariable Long id) {
        log.info("Getting user by id: {}", id);
        return userService.findById(id);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     * <p>
     * HTTP метод: DELETE /users/{id}
     * </p>
     *
     * @param id идентификатор пользователя для удаления (из пути запроса)
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserById(@PathVariable Long id) {
        log.info("Deleting user: {}", id);
        userService.deleteById(id);
    }
}