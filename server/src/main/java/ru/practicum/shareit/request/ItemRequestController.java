package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * REST-контроллер для управления запросами вещей в модуле server.
 * <p>
 * Предоставляет endpoints для создания запросов, получения запросов
 * конкретного пользователя, получения конкретного запроса по ID
 * и получения всех запросов.
 * </p>
 *
 * @see ItemRequestService
 * @see ItemRequestCreateDto
 * @see ItemRequestResponseDto
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    /**
     * Создает новый запрос вещи.
     * <p>
     * HTTP метод: POST /requests
     * </p>
     *
     * @param dto    DTO с описанием желаемой вещи
     * @param userId идентификатор пользователя, создающего запрос (из заголовка X-Sharer-User-Id)
     * @return созданный запрос в виде DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestResponseDto addRequest(
            @RequestBody ItemRequestCreateDto dto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Adding new request by user: {}", userId);
        return itemRequestService.save(userId, dto);
    }

    /**
     * Получает список всех запросов, созданных конкретным пользователем.
     * <p>
     * HTTP метод: GET /requests
     * </p>
     *
     * @param userId идентификатор пользователя (из заголовка X-Sharer-User-Id)
     * @return список запросов пользователя с предложенными вещами
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestResponseDto> getRequestsByUserId(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get requests by user: {}", userId);
        return itemRequestService.findByUserId(userId);
    }

    /**
     * Получает информацию о конкретном запросе по его идентификатору.
     * <p>
     * HTTP метод: GET /requests/{requestId}
     * </p>
     *
     * @param requestId идентификатор запроса (из пути запроса)
     * @return информация о запросе с предложенными вещами
     */
    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestResponseDto getRequestsById(
            @PathVariable Long requestId) {
        log.info("Get request by request id: {}", requestId);
        return itemRequestService.findById(requestId);
    }

    /**
     * Получает список всех запросов, созданных другими пользователями.
     * <p>
     * HTTP метод: GET /requests/all
     * Используется для просмотра доступных запросов, на которые можно предложить свои вещи.
     * </p>
     *
     * @return список всех запросов с предложенными вещами
     */
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestResponseDto> getAllRequests() {
        log.info("Get all requests");
        return itemRequestService.findAll();
    }
}