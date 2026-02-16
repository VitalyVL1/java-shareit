package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateOrUpdateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

/**
 * Контроллер для обработки HTTP-запросов, связанных с вещами и комментариями, в модуле gateway.
 * <p>
 * Выполняет первичную валидацию входящих данных и перенаправляет запросы
 * в соответствующие методы клиента {@link ItemClient}.
 * </p>
 *
 * @see ItemClient
 * @see ItemCreateDto
 * @see ItemUpdateDto
 * @see CommentCreateOrUpdateDto
 */
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    /**
     * Добавляет новую вещь.
     * <p>
     * HTTP метод: POST /items
     * </p>
     *
     * @param dto    DTO с данными для создания вещи (название, описание, доступность, опционально requestId)
     * @param userId идентификатор владельца вещи (из заголовка X-Sharer-User-Id)
     * @return {@link ResponseEntity} с созданной вещью
     */
    @PostMapping
    public ResponseEntity<Object> addItem(
            @Valid @RequestBody ItemCreateDto dto,
            @RequestHeader("X-Sharer-User-Id") @Positive @NotNull Long userId
    ) {
        log.info("Adding new item: {}", dto);
        return itemClient.addItem(userId, dto);
    }

    /**
     * Обновляет существующую вещь.
     * <p>
     * HTTP метод: PATCH /items/{itemId}
     * </p>
     *
     * @param dto    DTO с обновляемыми полями (все поля опциональны)
     * @param itemId идентификатор обновляемой вещи (из пути запроса)
     * @param userId идентификатор владельца вещи (из заголовка X-Sharer-User-Id)
     * @return {@link ResponseEntity} с обновленной вещью
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @Valid @RequestBody ItemUpdateDto dto,
            @PathVariable @Positive Long itemId,
            @RequestHeader("X-Sharer-User-Id") @Positive @NotNull Long userId
    ) {
        log.info("Updating existing item: {}", dto);
        return itemClient.updateItem(userId, itemId, dto);
    }

    /**
     * Получает список всех вещей конкретного владельца.
     * <p>
     * HTTP метод: GET /items
     * </p>
     *
     * @param ownerId идентификатор владельца вещей (из заголовка X-Sharer-User-Id)
     * @return {@link ResponseEntity} со списком вещей владельца
     */
    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") @Positive @NotNull Long ownerId
    ) {
        log.info("Retrieving items by owner: {}", ownerId);
        return itemClient.getItemsByOwner(ownerId);
    }

    /**
     * Получает информацию о конкретной вещи по её идентификатору.
     * <p>
     * HTTP метод: GET /items/{itemId}
     * </p>
     *
     * @param itemId идентификатор вещи (из пути запроса)
     * @param userId идентификатор пользователя, запрашивающего информацию (из заголовка X-Sharer-User-Id)
     * @return {@link ResponseEntity} с данными вещи, включая комментарии и даты бронирований (если пользователь - владелец)
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @PathVariable @Positive Long itemId,
            @RequestHeader("X-Sharer-User-Id") @Positive @NotNull Long userId
    ) {
        log.info("Retrieving item by id: {}, by user: {}", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    /**
     * Выполняет поиск вещей по тексту в названии или описании.
     * <p>
     * HTTP метод: GET /items/search?text={text}
     * Поиск доступен только для доступных вещей (available = true).
     * </p>
     *
     * @param text текст для поиска (из query-параметра)
     * @return {@link ResponseEntity} со списком найденных вещей
     */
    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam("text") String text) {
        log.info("Searching items by text: {}", text);
        return itemClient.searchItems(text);
    }

    /**
     * Добавляет комментарий к вещи от пользователя, который её арендовал.
     * <p>
     * HTTP метод: POST /items/{itemId}/comment
     * Комментарий можно оставить только после завершения бронирования.
     * </p>
     *
     * @param authorId идентификатор автора комментария (из заголовка X-Sharer-User-Id)
     * @param itemId   идентификатор вещи, к которой оставляется комментарий (из пути запроса)
     * @param dto      DTO с текстом комментария
     * @return {@link ResponseEntity} с созданным комментарием
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader("X-Sharer-User-Id") @Positive @NotNull Long authorId,
            @PathVariable @NotNull Long itemId,
            @RequestBody @Valid CommentCreateOrUpdateDto dto) {
        log.info("Adding comment to item {} by author {}: {}", itemId, authorId, dto);

        return itemClient.addComment(authorId, itemId, dto);
    }
}