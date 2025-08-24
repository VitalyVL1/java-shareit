package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateOrUpdateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(
            @Valid @RequestBody ItemCreateDto dto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Adding new item: {}", dto);
        return itemClient.addItem(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @Valid @RequestBody ItemUpdateDto dto,
            @PathVariable @Valid @NotNull @Positive Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Updating existing item: {}", dto);
        return itemClient.updateItem(userId, itemId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Retrieving items by owner: {}", ownerId);
        return itemClient.getItemsByOwner(ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @PathVariable @Valid @Positive Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Retrieving item by id: {}, by user: {}", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam("text") String text) {
        log.info("Searching items by text: {}", text);
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader("X-Sharer-User-Id") Long authorId,
            @PathVariable Long itemId,
            @RequestBody @Valid CommentCreateOrUpdateDto dto) {
        log.info("Adding comment to item {} by author {}: {}", itemId, authorId, dto);

        return itemClient.addComment(authorId, itemId, dto);
    }
}
