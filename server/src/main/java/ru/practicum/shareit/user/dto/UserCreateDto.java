package ru.practicum.shareit.user.dto;

import lombok.Builder;

public record UserCreateDto(
        String name,
        String email
) {
    @Builder
    public UserCreateDto {
    }
}
