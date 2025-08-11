package ru.practicum.shareit.user.dto;

import lombok.Builder;

public record UserResponseDto(
        Long id,
        String name,
        String email
) {
    @Builder
    public static UserResponseDto of(Long id, String name, String email) {
        return new UserResponseDto(id, name, email);
    }
}
