package ru.practicum.shareit.request.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    Long id;

    @NotNull(message = "Описание должно быть указано")
    String description;

    @NotNull(message = "Пользователь должен быть указан")
    User requestor;

    @Builder.Default
    LocalDateTime created = LocalDateTime.now();
}
