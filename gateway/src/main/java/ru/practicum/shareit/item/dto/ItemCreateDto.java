package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * DTO для создания новой вещи в модуле gateway.
 * <p>
 * Содержит данные, необходимые для создания вещи: название, описание,
 * статус доступности и опционально идентификатор запроса (если вещь создается
 * в ответ на запрос другого пользователя).
 * </p>
 *
 * @param name        название вещи (не может быть {@code null} или пустым)
 * @param description описание вещи (не может быть {@code null} или пустым)
 * @param available   флаг доступности вещи для аренды (не может быть {@code null})
 * @param requestId   идентификатор запроса, на который отвечает данная вещь (может быть {@code null})
 *
 */
public record ItemCreateDto(
        @NotBlank(message = "Название должно быть указано")
        String name,

        @NotBlank(message = "Описание должно быть указано")
        String description,

        @NotNull(message = "Доступность вещи должна быть задана")
        Boolean available,

        Long requestId
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать экземпляры DTO с удобной цепочкой вызовов,
     * особенно когда некоторые поля (например, {@code requestId}) могут быть опущены.
     */
    @Builder
    public ItemCreateDto {
    }
}