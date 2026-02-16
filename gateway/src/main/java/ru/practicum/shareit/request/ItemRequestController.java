package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

/**
 * Контроллер для обработки HTTP-запросов, связанных с запросами вещей, в модуле gateway.
 * <p>
 * Выполняет первичную валидацию входящих данных и перенаправляет запросы
 * в соответствующие методы клиента {@link RequestClient}.
 * </p>
 *
 * @see RequestClient
 * @see ItemRequestCreateDto
 */
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final RequestClient requestClient;

    /**
     * Создает новый запрос вещи.
     * <p>
     * HTTP метод: POST /requests
     * </p>
     *
     * @param dto    DTO с описанием запрашиваемой вещи
     * @param userId идентификатор пользователя, создающего запрос (из заголовка X-Sharer-User-Id)
     * @return {@link ResponseEntity} с созданным запросом
     */
    @PostMapping
    public ResponseEntity<Object> addRequest(
            @Valid @RequestBody ItemRequestCreateDto dto,
            @RequestHeader("X-Sharer-User-Id") @Positive @NotNull Long userId
    ) {
        log.info("Adding new request by user: {}", userId);
        return requestClient.addRequest(userId, dto);
    }

    /**
     * Получает список всех запросов, созданных конкретным пользователем.
     * <p>
     * HTTP метод: GET /requests
     * </p>
     *
     * @param userId идентификатор пользователя (из заголовка X-Sharer-User-Id)
     * @return {@link ResponseEntity} со списком запросов пользователя
     */
    @GetMapping
    public ResponseEntity<Object> getRequestsByUserId(
            @RequestHeader("X-Sharer-User-Id") @Positive @NotNull Long userId) {
        log.info("Get requests by user: {}", userId);
        return requestClient.getRequestsByUserId(userId);
    }

    /**
     * Получает информацию о конкретном запросе по его идентификатору.
     * <p>
     * HTTP метод: GET /requests/{requestId}
     * </p>
     *
     * @param requestId идентификатор запроса (из пути запроса)
     * @return {@link ResponseEntity} с данными запроса
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestsById(
            @PathVariable @Positive Long requestId) {
        log.info("Get request by request id: {}", requestId);
        return requestClient.getRequestsById(requestId);
    }

    /**
     * Получает список всех запросов, созданных другими пользователями.
     * <p>
     * HTTP метод: GET /requests/all
     * Используется для просмотра доступных запросов, на которые можно предложить свои вещи.
     * </p>
     *
     * @return {@link ResponseEntity} со списком всех запросов
     */
    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests() {
        log.info("Get all requests");
        return requestClient.getAllRequests();
    }
}