package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

/**
 * Утилитарный класс для преобразования между сущностью {@link User} и соответствующими DTO.
 * <p>
 * Содержит статические методы для преобразования объектов пользователей в различные
 * форматы DTO и обратно. Используется в сервисном слое для изоляции логики
 * преобразования и предотвращения циклических зависимостей.
 * </p>
 *
 * @see User
 * @see UserResponseDto
 * @see UserCreateDto
 */
public class UserMapper {

    /**
     * Преобразует сущность {@link User} в {@link UserResponseDto}.
     *
     * @param user сущность пользователя (может быть {@code null})
     * @return DTO с информацией о пользователе или {@code null}, если входной параметр равен {@code null}
     */
    public static UserResponseDto toUserResponseDto(User user) {
        if (user == null) return null;

        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    /**
     * Создает сущность {@link User} из DTO создания.
     * <p>
     * Используется при регистрации нового пользователя. ID не устанавливается,
     * так как будет сгенерирован автоматически при сохранении.
     * </p>
     *
     * @param dto DTO с данными для создания пользователя
     * @return новая сущность пользователя или {@code null}, если DTO равен {@code null}
     */
    public static User toUser(UserCreateDto dto) {
        if (dto == null) return null;

        return User.builder()
                .name(dto.name())
                .email(dto.email())
                .build();
    }

    /**
     * Преобразует список сущностей {@link User} в список {@link UserResponseDto}.
     * <p>
     * Применяет {@link #toUserResponseDto(User)} к каждому элементу списка.
     * </p>
     *
     * @param users список сущностей пользователей (может быть {@code null} или пустым)
     * @return список DTO с информацией о пользователях или пустой список,
     *         если входной параметр равен {@code null} или пуст
     */
    public static List<UserResponseDto> toUserResponseDto(List<User> users) {
        if (users == null || users.isEmpty()) return Collections.emptyList();
        return users.stream()
                .map(UserMapper::toUserResponseDto)
                .toList();
    }
}