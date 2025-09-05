package ru.practicum.shareit.booking.dto;

import lombok.Builder;

public record BookingApproveDto(
        Long id,
        Long ownerId,
        Long itemId,
        Boolean isApproved
) {
    @Builder
    public BookingApproveDto {
    }
}
