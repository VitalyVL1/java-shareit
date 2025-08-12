package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;

public record UserUpdateDto(
        String name,
        @Email String email
) {
    @Builder
    public UserUpdateDto {
    }
}
