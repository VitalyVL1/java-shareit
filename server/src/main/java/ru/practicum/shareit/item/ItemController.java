package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto addItem(
            @Valid @RequestBody ItemCreateDto dto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Adding new item: {}", dto);
        return itemService.save(userId, dto);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponseDto updateItem(
            @Valid @RequestBody ItemUpdateDto dto,
            @PathVariable @Valid @NonNull @Positive Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Updating existing item: {}", dto);
        UpdateItemCommand command = UpdateItemCommand.of(userId, itemId, dto);
        return itemService.update(command);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponseDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Retrieving items by owner: {}", userId);
        return itemService.findByUserId(userId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponseWithCommentsDto getItemById(
            @PathVariable @Valid @Positive Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Retrieving item by id: {}, by user: {}", itemId, userId);
        return itemService.findById(itemId, userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponseDto> searchItems(@RequestParam("text") String text) {
        log.info("Searching items by text: {}", text);
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentRequestDto addComment(
            @RequestHeader("X-Sharer-User-Id") Long authorId,
            @PathVariable Long itemId,
            @RequestBody @Valid CommentCreateOrUpdateDto dto) {
        log.info("Adding comment to item {} by author {}: {}", itemId, authorId, dto);
        CreateCommentCommand command = CreateCommentCommand.builder()
                .authorId(authorId)
                .itemId(itemId)
                .dto(dto)
                .build();
        return itemService.addComment(command);
    }
}
