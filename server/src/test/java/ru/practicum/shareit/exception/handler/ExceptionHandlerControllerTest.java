package ru.practicum.shareit.exception.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.exception.response.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlerControllerTest {

    private static final String ENTITY_NAME = "User";
    private static final long ENTITY_ID = 1;
    private static final String MESSAGE = "Message";


    @InjectMocks
    private ExceptionHandlerController exceptionHandler;

    @Test
    void handleNotFoundException_shouldReturnNotFoundResponse() {
        NotFoundException exception = new NotFoundException(ENTITY_NAME, ENTITY_ID);
        ErrorResponse response = exceptionHandler.handleNotFoundException(exception);
        assertEquals(ENTITY_NAME, response.code());
        assertTrue(response.message().contains(exception.getMessage()));
    }

    @Test
    void handleUnavailableItemException_shouldReturnBadRequestResponse() {
        UnavailableItemException exception = new UnavailableItemException(ENTITY_ID, MESSAGE);
        ErrorResponse response = exceptionHandler.handleUnavailableItemException(exception);

        assertEquals("ITEM_UNAVAILABLE", response.code());
        assertTrue(response.message().contains(exception.getMessage()));
    }

    @Test
    void handleCommentNotAllowedException_shouldReturnBadRequestResponse() {
        CommentNotAllowedException exception = new CommentNotAllowedException(MESSAGE, ENTITY_ID, 2L);
        ErrorResponse response = exceptionHandler.handleCommentNotAllowedException(exception);

        assertEquals("COMMENT_NOT_ALLOWED", response.code());
        assertTrue(response.message().contains(exception.getMessage()));
    }

    @Test
    void handleAccessForbiddenException_shouldReturnForbiddenResponse() {
        AccessForbiddenException exception = new AccessForbiddenException(MESSAGE, ENTITY_ID);
        ErrorResponse response = exceptionHandler.handleAccessForbiddenException(exception);
        assertEquals("ACCESS_FORBIDDEN", response.code());
        assertTrue(response.message().contains(exception.getMessage()));
    }

    @Test
    void handleDuplicatedDataException_shouldReturnConflictResponse() {
        DuplicatedDataException exception = new DuplicatedDataException(ENTITY_NAME, MESSAGE);
        ErrorResponse response = exceptionHandler.handleDuplicatedDataException(exception);
        assertEquals(ENTITY_NAME, response.code());
        assertTrue(response.message().contains(exception.getMessage()));
    }

    @Test
    void handleNoContentException_shouldReturnNoContentResponse() {
        NoContentException exception = new NoContentException(MESSAGE);
        ErrorResponse response = exceptionHandler.handleNoContentException(exception);
        assertEquals("NO_CONTENT", response.code());
        assertTrue(response.message().contains(exception.getMessage()));
    }

    @Test
    void handleAllExceptions_shouldReturnInternalErrorResponse() {
        Exception exception = new RuntimeException(MESSAGE);
        ErrorResponse response = exceptionHandler.handleAllExceptions(exception);
        assertEquals("internal-error", response.code());
        assertEquals("An unexpected error occurred", response.message());
    }

    @Test
    void handleAllExceptions_withDifferentException_shouldReturnInternalError() {
        Exception exception = new IllegalArgumentException("Invalid argument");
        ErrorResponse response = exceptionHandler.handleAllExceptions(exception);
        assertEquals("internal-error", response.code());
        assertEquals("An unexpected error occurred", response.message());
    }
}