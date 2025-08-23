package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class ItemMapper {
    public static ItemResponseDto toItemResponseDto(Item item) {
        if (item == null) return null;

        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(
                        item.getRequest() != null ?
                                item.getRequest().getId() : null)
                .build();
    }

    public static List<ItemResponseDto> toItemResponseDto(List<Item> items) {
        if (items == null || items.isEmpty()) return Collections.emptyList();
        return items.stream()
                .map(ItemMapper::toItemResponseDto)
                .toList();
    }

    public static ItemShortDto toItemShortDto(Item item) {
        if (item == null) return null;

        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public static List<ItemShortDto> toItemShortDto(List<Item> items) {
        if (items == null || items.isEmpty()) return Collections.emptyList();

        return items.stream()
                .map(ItemMapper::toItemShortDto)
                .toList();
    }

    public static ItemResponseWithCommentsDto toItemResponseWithCommentsDto(
            Item item,
            LocalDateTime lastBooking,
            LocalDateTime nextBooking,
            List<CommentRequestDto> comments) {
        if (item == null) return null;

        return ItemResponseWithCommentsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(
                        item.getRequest() != null ?
                                item.getRequest().getId() : null)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

    public static Item toItem(User owner, ItemRequest request, ItemCreateDto dto) {
        if (dto == null) return null;

        return Item.builder()
                .name(dto.name())
                .description(dto.description())
                .available(dto.available())
                .owner(owner)
                .request(request)
                .build();
    }

    public static ItemForRequestDto toItemForRequestDto(Item item) {
        if (item == null) return null;
        return ItemForRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }

    public static List<ItemForRequestDto> toItemForRequestDto(List<Item> items) {
        if (items == null || items.isEmpty()) return Collections.emptyList();
        return items.stream()
                .map(ItemMapper::toItemForRequestDto)
                .toList();
    }
}
