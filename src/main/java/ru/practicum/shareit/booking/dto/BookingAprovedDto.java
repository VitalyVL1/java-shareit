package ru.practicum.shareit.booking.dto;

import lombok.Builder;

public record BookingAprovedDto(
        Long id,
        Long ownerId,
        Long itemId,
        Boolean isApproved
) {
    @Builder
    public BookingAprovedDto {
    }
}
