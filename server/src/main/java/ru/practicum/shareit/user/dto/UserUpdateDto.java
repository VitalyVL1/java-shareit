package ru.practicum.shareit.user.dto;

import lombok.Builder;

public record UserUpdateDto(
        String name,
        String email
) {
    @Builder
    public UserUpdateDto {
    }
}
