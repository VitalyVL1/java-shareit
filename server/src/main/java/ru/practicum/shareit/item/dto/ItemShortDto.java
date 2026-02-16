package ru.practicum.shareit.item.dto;

import lombok.Builder;

/**
 * DTO для отображения краткой информации о вещи в модуле server.
 * <p>
 * Используется в тех случаях, когда требуется только идентификатор и название вещи,
 * например, при встраивании информации о вещи в другие DTO (бронирования, запросы).
 * Минимизирует объем передаваемых данных и предотвращает циклические ссылки.
 * </p>
 *
 * @param id   идентификатор вещи
 * @param name название вещи
 *
 * @see ru.practicum.shareit.booking.dto.BookingResponseDto
 * @see ru.practicum.shareit.item.model.Item
 */
public record ItemShortDto(
        Long id,
        String name
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать экземпляры DTO с удобной цепочкой вызовов.
     */
    @Builder
    public ItemShortDto {
    }
}