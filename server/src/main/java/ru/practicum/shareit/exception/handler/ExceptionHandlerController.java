package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.exception.response.ErrorResponse;

/**
 * Глобальный обработчик исключений для модуля server.
 * <p>
 * Перехватывает исключения, возникающие в процессе обработки запросов в серверном модуле,
 * и преобразует их в стандартизированные ответы с соответствующими HTTP-статусами.
 * Обрабатывает различные типы исключений, специфичные для бизнес-логики приложения.
 * </p>
 *
 * @see ErrorResponse
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    /**
     * Обрабатывает исключения типа {@link NotFoundException}.
     * <p>
     * Возникает, когда запрашиваемая сущность (пользователь, вещь, бронирование и т.д.)
     * не найдена в базе данных.
     * </p>
     *
     * @param e исключение {@link NotFoundException}, содержащее информацию о ненайденной сущности
     * @return {@link ErrorResponse} с кодом статуса 404 (NOT_FOUND)
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.warn("Not found: {}", e.getMessage(), e);
        return new ErrorResponse(e.getEntityName(), e.getMessage());
    }

    /**
     * Обрабатывает исключения типа {@link UnavailableItemException}.
     * <p>
     * Возникает при попытке забронировать вещь, которая недоступна для аренды
     * (например, уже забронирована на указанный период или помечена как недоступная).
     * </p>
     *
     * @param ex исключение {@link UnavailableItemException}
     * @return {@link ErrorResponse} с кодом статуса 400 (BAD_REQUEST)
     */
    @ExceptionHandler(UnavailableItemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnavailableItemException(UnavailableItemException ex) {
        log.warn("Failed to book item (ID: {}): {}", ex.getItemId(), ex.getMessage(), ex);
        return new ErrorResponse(
                "ITEM_UNAVAILABLE",
                "Item with ID " + ex.getItemId() + " is not available: " + ex.getMessage()
        );
    }

    /**
     * Обрабатывает исключения типа {@link CommentNotAllowedException}.
     * <p>
     * Возникает при попытке оставить комментарий к вещи, если пользователь
     * не брал эту вещь в аренду или бронирование ещё не завершено.
     * </p>
     *
     * @param ex исключение {@link CommentNotAllowedException}
     * @return {@link ErrorResponse} с кодом статуса 400 (BAD_REQUEST)
     */
    @ExceptionHandler(CommentNotAllowedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCommentNotAllowedException(CommentNotAllowedException ex) {
        log.warn("Comment not allowed: {}", ex.getMessage(), ex);
        return new ErrorResponse(
                "COMMENT_NOT_ALLOWED",
                "Comment to " + ex.getItemId() + " is not allowed to user: " + ex.getUserId()
                + "! Reason: " + ex.getMessage()
        );
    }

    /**
     * Обрабатывает исключения типа {@link AccessForbiddenException}.
     * <p>
     * Возникает при попытке доступа к ресурсу, к которому у пользователя нет прав
     * (например, владелец пытается забронировать свою вещь, или пользователь
     * пытается получить доступ к чужому бронированию).
     * </p>
     *
     * @param ex исключение {@link AccessForbiddenException}
     * @return {@link ErrorResponse} с кодом статуса 403 (FORBIDDEN)
     */
    @ExceptionHandler(AccessForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessForbiddenException(AccessForbiddenException ex) {
        log.warn("Failed to access item by user: {}: {}", ex.getUserId(), ex.getMessage(), ex);
        return new ErrorResponse(
                "ACCESS_FORBIDDEN",
                ex.getMessage()
        );
    }

    /**
     * Обрабатывает исключения типа {@link DuplicatedDataException}.
     * <p>
     * Возникает при попытке создать или обновить сущность с данными,
     * которые уже существуют (например, email пользователя).
     * </p>
     *
     * @param e исключение {@link DuplicatedDataException}
     * @return {@link ErrorResponse} с кодом статуса 409 (CONFLICT)
     */
    @ExceptionHandler(DuplicatedDataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicatedDataException(DuplicatedDataException e) {
        log.warn(e.getMessage(), e);
        return new ErrorResponse(e.getFieldName(), e.getMessage());
    }

    /**
     * Обрабатывает исключения типа {@link NoContentException}.
     * <p>
     * Возникает, когда запрос выполнен успешно, но не найден контент для возврата
     * (например, у пользователя нет бронирований). Возвращает статус 204 NO_CONTENT.
     * </p>
     *
     * @param e исключение {@link NoContentException}
     * @return {@link ErrorResponse} с кодом статуса 204 (NO_CONTENT)
     */
    @ExceptionHandler(NoContentException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ErrorResponse handleNoContentException(NoContentException e) {
        log.warn(e.getMessage(), e);
        return new ErrorResponse("NO_CONTENT", e.getMessage());
    }

    /**
     * Обрабатывает все исключения, не учтенные выше.
     * <p>
     * Является обработчиком по умолчанию для непредвиденных ошибок.
     * Возвращает общий ответ с кодом статуса 500 (INTERNAL_SERVER_ERROR).
     * </p>
     *
     * @param e исключение {@link Exception}
     * @return {@link ErrorResponse} с кодом статуса 500 (INTERNAL_SERVER_ERROR)
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllExceptions(Exception e) {
        log.warn("Unexpected error: {}", e.getMessage(), e);
        return new ErrorResponse("internal-error", "An unexpected error occurred");
    }
}