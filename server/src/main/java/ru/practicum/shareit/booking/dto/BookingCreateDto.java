package ru.practicum.shareit.booking.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public record BookingCreateDto(
        Long itemId,
        LocalDateTime start,
        LocalDateTime end
) {
    @Builder
    public BookingCreateDto {
    }
}
