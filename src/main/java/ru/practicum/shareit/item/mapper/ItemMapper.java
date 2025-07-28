package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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
                .request(
                        item.getRequest() != null ?
                                item.getRequest().getId() : null)
                .build();
    }

    public static Item toItem(User user, ItemCreateDto dto) {
        if (dto == null) return null;

        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .request(dto.getRequest()) //если данные будут передаваться в теле запроса
                .owner(user)
                .build();
    }

    public static List<ItemResponseDto> toItemResponseDtoList(List<Item> items) {
        if (items == null || items.isEmpty()) return Collections.emptyList();
        return items.stream()
                .map(ItemMapper::toItemResponseDto)
                .toList();
    }
}
