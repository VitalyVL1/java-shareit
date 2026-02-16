package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * REST-контроллер для управления вещами и комментариями в модуле server.
 * <p>
 * Предоставляет endpoints для создания, обновления, получения и поиска вещей,
 * а также для добавления комментариев к вещам.
 * </p>
 *
 * @see ItemService
 * @see ItemCreateDto
 * @see ItemUpdateDto
 * @see ItemResponseDto
 * @see ItemResponseWithCommentsDto
 * @see CommentCreateOrUpdateDto
 * @see CommentRequestDto
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    /**
     * Создает новую вещь.
     * <p>
     * HTTP метод: POST /items
     * </p>
     *
     * @param dto    DTO с данными для создания вещи
     * @param userId идентификатор владельца вещи (из заголовка X-Sharer-User-Id)
     * @return созданная вещь в виде базового DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto addItem(
            @RequestBody ItemCreateDto dto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Adding new item: {}", dto);
        return itemService.save(userId, dto);
    }

    /**
     * Обновляет существующую вещь.
     * <p>
     * HTTP метод: PATCH /items/{itemId}
     * </p>
     *
     * @param dto    DTO с обновляемыми полями
     * @param itemId идентификатор обновляемой вещи (из пути запроса)
     * @param userId идентификатор владельца вещи (из заголовка X-Sharer-User-Id)
     * @return обновленная вещь в виде базового DTO
     */
    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponseDto updateItem(
            @RequestBody ItemUpdateDto dto,
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Updating existing item: {}", dto);
        UpdateItemCommand command = UpdateItemCommand.of(userId, itemId, dto);
        return itemService.update(command);
    }

    /**
     * Получает список всех вещей конкретного владельца.
     * <p>
     * HTTP метод: GET /items
     * Для каждой вещи возвращается расширенная информация с датами ближайших бронирований.
     * </p>
     *
     * @param ownerId идентификатор владельца вещей (из заголовка X-Sharer-User-Id)
     * @return список вещей владельца с расширенной информацией
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponseWithCommentsDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Retrieving items by owner: {}", ownerId);
        return itemService.findByUserId(ownerId);
    }

    /**
     * Получает информацию о конкретной вещи по ее идентификатору.
     * <p>
     * HTTP метод: GET /items/{itemId}
     * Если запрос делает владелец вещи, в ответ добавляется информация о бронированиях.
     * </p>
     *
     * @param itemId идентификатор вещи (из пути запроса)
     * @param userId идентификатор пользователя, запрашивающего информацию (из заголовка X-Sharer-User-Id)
     * @return расширенная информация о вещи с комментариями и (для владельца) датами бронирований
     */
    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponseWithCommentsDto getItemById(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Retrieving item by id: {}, by user: {}", itemId, userId);
        return itemService.findById(itemId, userId);
    }

    /**
     * Выполняет поиск доступных вещей по тексту в названии или описании.
     * <p>
     * HTTP метод: GET /items/search?text={text}
     * Поиск доступен только для доступных вещей (available = true).
     * </p>
     *
     * @param text текст для поиска (из query-параметра)
     * @return список найденных вещей в виде базовых DTO
     */
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponseDto> searchItems(@RequestParam("text") String text) {
        log.info("Searching items by text: {}", text);
        return itemService.search(text);
    }

    /**
     * Добавляет комментарий к вещи.
     * <p>
     * HTTP метод: POST /items/{itemId}/comment
     * Комментарий можно оставить только после завершения бронирования.
     * </p>
     *
     * @param authorId идентификатор автора комментария (из заголовка X-Sharer-User-Id)
     * @param itemId   идентификатор вещи, к которой оставляется комментарий (из пути запроса)
     * @param dto      DTO с текстом комментария
     * @return созданный комментарий в виде DTO
     */
    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentRequestDto addComment(
            @RequestHeader("X-Sharer-User-Id") Long authorId,
            @PathVariable Long itemId,
            @RequestBody CommentCreateOrUpdateDto dto) {
        log.info("Adding comment to item {} by author {}: {}", itemId, authorId, dto);
        CreateCommentCommand command = CreateCommentCommand.builder()
                .authorId(authorId)
                .itemId(itemId)
                .dto(dto)
                .build();
        return itemService.addComment(command);
    }
}