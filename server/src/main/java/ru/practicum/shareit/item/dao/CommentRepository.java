package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

/**
 * Репозиторий для управления сущностями {@link Comment} в базе данных.
 * <p>
 * Предоставляет методы для выполнения операций с комментариями,
 * включая поиск комментариев по идентификатору вещи.
 * </p>
 *
 * @see Comment
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Находит все комментарии, оставленные к указанной вещи.
     * <p>
     * Используется при формировании полной информации о вещи,
     * чтобы отобразить все комментарии пользователей.
     * </p>
     *
     * @param itemId идентификатор вещи, для которой ищутся комментарии
     * @return список комментариев к указанной вещи (может быть пустым)
     */
    List<Comment> findAllByItem_Id(Long itemId);
}