package ru.practicum.shareit.exception.handler;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.response.ErrorResponse;
import ru.practicum.shareit.exception.response.ValidationErrorResponse;
import ru.practicum.shareit.exception.response.Violation;

import java.util.List;

/**
 * Глобальный обработчик исключений для модуля gateway.
 * <p>
 * Перехватывает исключения, возникающие в процессе обработки запросов,
 * и преобразует их в стандартизированные ответы с соответствующими HTTP-статусами.
 * Обрабатывает ошибки валидации и некорректные аргументы.
 * </p>
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    /**
     * Обрабатывает исключения, возникающие при провале валидации с аннотацией {@link Valid}.
     * <p>
     * Срабатывает, когда объект, помеченный {@code @Valid}, не проходит валидацию
     * (например, нарушения {@code @NotNull}, {@code @Size} и т.д. в теле запроса).
     * Возвращает подробный ответ со списком нарушений (поле, сообщение, отклоненное значение).
     * </p>
     *
     * @param e исключение {@link MethodArgumentNotValidException}, содержащее детали ошибок валидации
     * @return {@link ValidationErrorResponse} с кодом статуса 400 (BAD_REQUEST)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Validation error: {}", e.getMessage(), e);

        List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()))
                .toList();

        return new ValidationErrorResponse("Validation failed", violations);
    }

    /**
     * Обрабатывает исключения, связанные с нарушением ограничений для параметров методов.
     * <p>
     * Срабатывает для аннотаций валидации на параметрах методов контроллера,
     * таких как {@code @RequestParam}, {@code @PathVariable}, {@code @RequestHeader}.
     * Возвращает подробный ответ со списком нарушений.
     * </p>
     *
     * @param e исключение {@link ConstraintViolationException} с деталями нарушений
     * @return {@link ValidationErrorResponse} с кодом статуса 400 (BAD_REQUEST)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleConstraintViolation(ConstraintViolationException e) {
        log.warn("Constraint violation: {}", e.getMessage(), e);

        List<Violation> violations = e.getConstraintViolations().stream()
                .map(violation -> new Violation(
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        violation.getInvalidValue()))
                .toList();

        return new ValidationErrorResponse("Constraint violation", violations);
    }

    /**
     * Обрабатывает исключения типа {@link IllegalArgumentException}.
     * <p>
     * Используется для обработки случаев, когда передан некорректный аргумент,
     * например, неизвестный статус при преобразовании строки в {@link ru.practicum.shareit.booking.dto.State}.
     * Возвращает общий ответ с сообщением об ошибке.
     * </p>
     *
     * @param e исключение {@link IllegalArgumentException}
     * @return {@link ErrorResponse} с кодом статуса 400 (BAD_REQUEST)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Exception: {}", e.getMessage(), e);
        return new ErrorResponse("IllegalArgument", "Something went wrong");
    }
}