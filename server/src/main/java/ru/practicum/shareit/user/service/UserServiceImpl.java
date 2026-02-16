package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса {@link UserService} для управления пользователями.
 * <p>
 * Обеспечивает бизнес-логику для работы с пользователями: создание, получение,
 * обновление, удаление. Включает проверки на уникальность email и валидацию
 * существования пользователей.
 * </p>
 *
 * @see UserService
 * @see UserRepository
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    /**
     * Создает нового пользователя.
     * <p>
     * Проверяет, что email не занят другим пользователем.
     * </p>
     *
     * @param dto DTO с данными для создания пользователя (имя и email)
     * @return созданный пользователь в виде DTO
     * @throws DuplicatedDataException если пользователь с таким email уже существует
     */
    @Transactional
    @Override
    public UserResponseDto save(UserCreateDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new DuplicatedDataException("email", dto.email());
        }
        return UserMapper.toUserResponseDto(userRepository.save(UserMapper.toUser(dto)));
    }

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return найденный пользователь в виде DTO
     * @throws NotFoundException если пользователь с указанным ID не найден
     */
    @Override
    public UserResponseDto findById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toUserResponseDto)
                .orElseThrow(() -> new NotFoundException("User", id));
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список всех пользователей в виде DTO
     */
    @Override
    public List<UserResponseDto> findAll() {
        return UserMapper.toUserResponseDto(userRepository.findAll());
    }

    /**
     * Обновляет существующего пользователя.
     * <p>
     * Проверяет, что при обновлении email не занят другим пользователем.
     * Обновляет только те поля, которые были переданы в DTO (не {@code null}).
     * </p>
     *
     * @param userId идентификатор обновляемого пользователя
     * @param dto    DTO с обновляемыми полями (оба поля опциональны)
     * @return обновленный пользователь в виде DTO
     * @throws NotFoundException если пользователь с указанным ID не найден
     * @throws DuplicatedDataException если новый email уже занят другим пользователем
     */
    @Transactional
    @Override
    public UserResponseDto update(Long userId, UserUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        if (dto.email() != null &&
            !dto.email().equals(user.getEmail()) &&
            userRepository.existsByEmail(dto.email())) {
            throw new DuplicatedDataException("email", dto.email());
        }

        applyUpdates(user, dto);

        return UserMapper.toUserResponseDto(userRepository.save(user));
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя для удаления
     */
    @Transactional
    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Очищает всех пользователей из хранилища.
     * <p>
     * Используется в основном для тестирования.
     * </p>
     */
    @Transactional
    @Override
    public void clear() {
        userRepository.deleteAll();
    }

    /**
     * Применяет обновления к сущности пользователя.
     * <p>
     * Обновляет только те поля, которые были переданы в DTO (не {@code null}).
     * </p>
     *
     * @param user пользователь для обновления
     * @param dto  DTO с обновляемыми полями
     */
    private void applyUpdates(User user, UserUpdateDto dto) {
        Optional.ofNullable(dto.name()).ifPresent(user::setName);
        Optional.ofNullable(dto.email()).ifPresent(user::setEmail);
    }
}