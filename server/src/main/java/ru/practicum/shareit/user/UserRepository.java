package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

/**
 * Репозиторий для управления сущностями {@link User} в базе данных.
 * <p>
 * Предоставляет методы для выполнения операций с пользователями,
 * включая проверку существования пользователя по email.
 * </p>
 *
 * @see User
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Проверяет, существует ли пользователь с указанным email.
     * <p>
     * Используется для валидации уникальности email при создании
     * или обновлении пользователя.
     * </p>
     *
     * @param email email для проверки
     * @return {@code true}, если пользователь с таким email уже существует,
     *         иначе {@code false}
     */
    boolean existsByEmail(String email);
}