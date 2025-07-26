package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto addItem(@Valid @RequestBody ItemCreateDto dto) {

    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemResponseDto updateItem(
            @Valid @RequestBody ItemUpdateDto dto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponseDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {

    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponseDto> getItemById(@PathVariable Long itemId) {

    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponseDto> searchItems(@RequestParam("text") String text) {

    }
}
