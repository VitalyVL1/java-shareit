package ru.practicum.shareit.exception;

import lombok.Getter;
import ru.practicum.shareit.item.dto.CreateCommentCommand;

/**
 * Исключение, выбрасываемое при попытке оставить комментарий к вещи без права на это.
 * <p>
 * Возникает, когда пользователь пытается оставить комментарий к вещи, которую он
 * никогда не арендовал, или если соответствующее бронирование ещё не завершено.
 * Согласно бизнес-логике приложения, комментировать вещь можно только после
 * завершения бронирования.
 * </p>
 *
 * <p>
 * Обрабатывается глобальным обработчиком исключений и возвращает статус 400 BAD_REQUEST.
 * </p>
 *
 * @see ru.practicum.shareit.exception.handler.ExceptionHandlerController
 * @see ru.practicum.shareit.item.service.ItemServiceImpl#addComment(CreateCommentCommand)
 */
@Getter
public class CommentNotAllowedException extends RuntimeException {
    /**
     * Идентификатор пользователя, пытавшегося оставить комментарий.
     */
    private final Long userId;

    /**
     * Идентификатор вещи, к которой пытались оставить комментарий.
     */
    private final Long itemId;

    /**
     * Создает новое исключение с указанным сообщением и идентификаторами пользователя и вещи.
     *
     * @param message сообщение, описывающее причину исключения
     * @param userId  идентификатор пользователя, вызвавшего исключение
     * @param itemId  идентификатор вещи, к которой пытались оставить комментарий
     */
    public CommentNotAllowedException(String message, Long userId, Long itemId) {
        super(message);
        this.userId = userId;
        this.itemId = itemId;
    }
}