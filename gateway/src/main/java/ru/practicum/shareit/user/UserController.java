package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

/**
 * Контроллер для обработки HTTP-запросов, связанных с пользователями, в модуле gateway.
 * <p>
 * Выполняет первичную валидацию входящих данных и перенаправляет запросы
 * в соответствующие методы клиента {@link UserClient}.
 * </p>
 *
 * @see UserClient
 * @see UserCreateDto
 * @see UserUpdateDto
 */
@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    /**
     * Создает нового пользователя.
     * <p>
     * HTTP метод: POST /users
     * </p>
     *
     * @param dto DTO с данными для создания пользователя (имя и email)
     * @return {@link ResponseEntity} с созданным пользователем
     */
    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserCreateDto dto) {
        log.info("Adding new user: {}", dto);
        return userClient.addUser(dto);
    }

    /**
     * Обновляет существующего пользователя.
     * <p>
     * HTTP метод: PATCH /users/{id}
     * </p>
     *
     * @param dto DTO с обновляемыми полями (оба поля опциональны)
     * @param id  идентификатор обновляемого пользователя (из пути запроса)
     * @return {@link ResponseEntity} с обновленным пользователем
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(
            @Valid @RequestBody UserUpdateDto dto,
            @PathVariable @Positive Long id) {
        log.info("Updating user: {}", dto);
        return userClient.updateUser(id, dto);
    }

    /**
     * Получает список всех пользователей.
     * <p>
     * HTTP метод: GET /users
     * </p>
     *
     * @return {@link ResponseEntity} со списком всех пользователей
     */
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Getting all users");
        return userClient.getAllUsers();
    }

    /**
     * Получает информацию о конкретном пользователе по его идентификатору.
     * <p>
     * HTTP метод: GET /users/{id}
     * </p>
     *
     * @param id идентификатор пользователя (из пути запроса)
     * @return {@link ResponseEntity} с данными пользователя
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable @Positive Long id) {
        log.info("Getting user by id: {}", id);
        return userClient.getUserById(id);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     * <p>
     * HTTP метод: DELETE /users/{id}
     * </p>
     *
     * @param id идентификатор пользователя для удаления (из пути запроса)
     * @return {@link ResponseEntity} с подтверждением удаления
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable @Positive Long id) {
        log.info("Deleting user: {}", id);
        return userClient.deleteUserById(id);
    }
}