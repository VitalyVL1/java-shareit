package ru.practicum.shareit.request.dto;

import lombok.Builder;
import ru.practicum.shareit.item.dto.ItemForRequestDto;

import java.time.LocalDateTime;
import java.util.Set;

@Builder(toBuilder = true)
public record ItemRequestResponseDto(
        Long id,
        String description,
        Long requestorId,
        Set<ItemForRequestDto> items,
        LocalDateTime created
) {
    @Builder
    public ItemRequestResponseDto {
    }
}
