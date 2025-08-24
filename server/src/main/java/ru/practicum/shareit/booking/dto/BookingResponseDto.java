package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;

public record BookingResponseDto(
        Long id,
        LocalDateTime start,
        LocalDateTime end,
        ItemShortDto item,
        UserResponseDto booker,
        Status status
) {
    @Builder
    public BookingResponseDto {
    }
}
