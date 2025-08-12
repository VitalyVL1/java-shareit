package ru.practicum.shareit.exception.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.exception.response.ErrorResponse;
import ru.practicum.shareit.exception.response.ValidationErrorResponse;
import ru.practicum.shareit.exception.response.Violation;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    // Обработка валидации @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Validation error: {}", e.getMessage(), e);

        List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()))
                .collect(Collectors.toList());

        return new ValidationErrorResponse("Validation failed", violations);
    }

    // Обработка валидации для @RequestParam, @PathVariable
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleConstraintViolation(ConstraintViolationException e) {
        log.warn("Constraint violation: {}", e.getMessage(), e);

        List<Violation> violations = e.getConstraintViolations().stream()
                .map(violation -> new Violation(
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        violation.getInvalidValue()))
                .collect(Collectors.toList());

        return new ValidationErrorResponse("Constraint violation", violations);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.warn("Not found: {}", e.getMessage(), e);
        return new ErrorResponse(e.getEntityName(), e.getMessage());
    }

    @ExceptionHandler(UnavailableItemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnavailableItemException(UnavailableItemException ex) {
        log.warn("Failed to book item (ID: {}): {}", ex.getItemId(), ex.getMessage(), ex);
        return new ErrorResponse(
                "ITEM_UNAVAILABLE",
                "Item with ID " + ex.getItemId() + " is not available: " + ex.getMessage()
        );
    }

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

    @ExceptionHandler(AccessForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessForbiddenException(AccessForbiddenException ex) {
        log.warn("Failed to access item by user: {}: {}", ex.getUserId(), ex.getMessage(), ex);
        return new ErrorResponse(
                "ACCESS_FORBIDDEN",
                ex.getMessage()
        );
    }

    @ExceptionHandler(DuplicatedDataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicatedDataException(DuplicatedDataException e) {
        log.warn(e.getMessage(), e);
        return new ErrorResponse(e.getFieldName(), e.getMessage());
    }

    //Обработка всех исключений не учтенных выше
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllExceptions(Exception e) {
        log.warn("Unexpected error: {}", e.getMessage(), e);
        return new ErrorResponse("internal-error", "An unexpected error occurred");
    }
}
