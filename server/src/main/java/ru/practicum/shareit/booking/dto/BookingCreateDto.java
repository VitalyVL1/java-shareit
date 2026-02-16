package ru.practicum.shareit.booking.dto;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO для создания нового бронирования в модуле server.
 * <p>
 * Содержит данные, необходимые для создания бронирования: идентификатор вещи,
 * дату и время начала и окончания бронирования. Отличается от аналогичного DTO
 * в модуле gateway отсутствием аннотаций валидации, так как валидация уже
 * выполнена на уровне gateway.
 * </p>
 *
 * @param itemId идентификатор вещи, которую пользователь хочет забронировать
 * @param start  дата и время начала бронирования
 * @param end    дата и время окончания бронирования
 *
 * @see ru.practicum.shareit.booking.BookingController
 * @see ru.practicum.shareit.booking.BookingService
 */
public record BookingCreateDto(
        Long itemId,
        LocalDateTime start,
        LocalDateTime end
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать экземпляры DTO с удобной цепочкой вызовов.
     */
    @Builder
    public BookingCreateDto {
    }
}