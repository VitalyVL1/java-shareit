package ru.practicum.shareit.booking.dto;

import lombok.Builder;

/**
 * DTO для подтверждения или отклонения бронирования в модуле server.
 * <p>
 * Используется при обработке запроса на подтверждение бронирования владельцем вещи.
 * Содержит все необходимые данные для принятия решения: идентификаторы бронирования,
 * владельца и вещи, а также флаг подтверждения.
 * </p>
 *
 * @param id         идентификатор бронирования
 * @param ownerId    идентификатор владельца вещи (пользователь, подтверждающий бронирование)
 * @param itemId     идентификатор вещи, для которой подтверждается бронирование
 * @param isApproved флаг подтверждения: {@code true} - подтвердить, {@code false} - отклонить
 *
 * @see ru.practicum.shareit.booking.BookingController
 * @see ru.practicum.shareit.booking.BookingService
 */
public record BookingApproveDto(
        Long id,
        Long ownerId,
        Long itemId,
        Boolean isApproved
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать экземпляры DTO с удобной цепочкой вызовов.
     */
    @Builder
    public BookingApproveDto {
    }
}