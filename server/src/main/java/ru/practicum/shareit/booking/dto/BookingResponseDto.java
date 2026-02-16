package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;

/**
 * DTO для ответа с полной информацией о бронировании в модуле server.
 * <p>
 * Используется при возврате данных о бронировании клиенту. Содержит всю информацию
 * о бронировании: идентификатор, даты начала и окончания, информацию о вещи,
 * данные о пользователе, сделавшем бронирование, и статус бронирования.
 * </p>
 *
 * @param id       идентификатор бронирования
 * @param start    дата и время начала бронирования
 * @param end      дата и время окончания бронирования
 * @param item     краткая информация о вещи (идентификатор и название)
 * @param booker   информация о пользователе, сделавшем бронирование
 * @param status   текущий статус бронирования (WAITING, APPROVED, REJECTED, CANCELED)
 *
 * @see ru.practicum.shareit.booking.model.Booking
 * @see ru.practicum.shareit.item.dto.ItemShortDto
 * @see ru.practicum.shareit.user.dto.UserResponseDto
 * @see Status
 */
public record BookingResponseDto(
        Long id,
        LocalDateTime start,
        LocalDateTime end,
        ItemShortDto item,
        UserResponseDto booker,
        Status status
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать экземпляры DTO с удобной цепочкой вызовов.
     */
    @Builder
    public BookingResponseDto {
    }
}