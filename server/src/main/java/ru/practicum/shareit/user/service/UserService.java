package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

/**
 * Сервис для управления пользователями в приложении ShareIt.
 * <p>
 * Определяет контракт для выполнения операций с пользователями:
 * создание, получение по идентификатору, получение всех пользователей,
 * обновление, удаление и очистка.
 * </p>
 *
 * @see UserResponseDto
 * @see UserCreateDto
 * @see UserUpdateDto
 * @see ru.practicum.shareit.user.model.User
 */
public interface UserService {

    /**
     * Создает нового пользователя.
     *
     * @param dto DTO с данными для создания пользователя (имя и email)
     * @return созданный пользователь в виде DTO
     */
    UserResponseDto save(UserCreateDto dto);

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return найденный пользователь в виде DTO
     */
    UserResponseDto findById(Long id);

    /**
     * Возвращает список всех пользователей.
     *
     * @return список всех пользователей в виде DTO
     */
    List<UserResponseDto> findAll();

    /**
     * Обновляет существующего пользователя.
     * <p>
     * Обновляет только те поля, которые были переданы в DTO (не {@code null}).
     * </p>
     *
     * @param userId идентификатор обновляемого пользователя
     * @param dto    DTO с обновляемыми полями (оба поля опциональны)
     * @return обновленный пользователь в виде DTO
     */
    UserResponseDto update(Long userId, UserUpdateDto dto);

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя для удаления
     */
    void deleteById(Long id);

    /**
     * Очищает всех пользователей из хранилища.
     * <p>
     * Используется в основном для тестирования.
     * </p>
     */
    void clear();
}