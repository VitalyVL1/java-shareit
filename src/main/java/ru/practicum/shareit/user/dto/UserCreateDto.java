package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateDto {
    @NotBlank(message = "Имя должно быть указано")
    private String name;

    @Email(message = "Некорректный email")
    @NotBlank(message = "Email должен быть указан")
    private String email;
}
