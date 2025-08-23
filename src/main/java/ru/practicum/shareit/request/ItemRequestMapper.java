package ru.practicum.shareit.request;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestResponseDto toItemRequestResponseDto(ItemRequest itemRequest, List<Item> items) {
        if (itemRequest == null) return null;

        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorID(itemRequest.getRequestor().getId())
                .items(new HashSet<>(ItemMapper.toItemForRequestDto(items)))
                .created(itemRequest.getCreated())
                .build();
    }

    public static List<ItemRequestResponseDto> toItemRequestResponseDto(
            List<ItemRequest> itemRequests,
            Map<Long, List<Item>> itemsByRequestId
    ) {
        if (itemRequests == null) return Collections.emptyList();

        return itemRequests.stream()
                .map(itemRequest -> toItemRequestResponseDto(
                        itemRequest,
                        itemsByRequestId.getOrDefault(itemRequest.getId(), Collections.emptyList())
                ))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static ItemRequest toItemRequest(User requestor, ItemRequestCreateDto dto) {
        if (dto == null) return null;

        return ItemRequest.builder()
                .requestor(requestor)
                .description(dto.description())
                .build();
    }

}
