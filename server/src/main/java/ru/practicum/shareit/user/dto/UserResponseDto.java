package ru.practicum.shareit.user.dto;

import lombok.Builder;

public record UserResponseDto(
        Long id,
        String name,
        String email
) {
    @Builder
    public UserResponseDto {
    }
}
