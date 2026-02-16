package ru.practicum.shareit.booking.dto;

/**
 * Перечисление возможных статусов для фильтрации запросов бронирований в модуле server.
 * <p>
 * Используется в сервисном слое для определения того, бронирования с каким статусом
 * должен вернуть сервер. Приходит из gateway после преобразования из строкового параметра запроса.
 * </p>
 *
 * <p><b>Значения перечисления:</b></p>
 * <ul>
 *   <li>{@link #ALL} - все бронирования (без фильтрации по статусу)</li>
 *   <li>{@link #CURRENT} - текущие бронирования (начались, но ещё не закончились)</li>
 *   <li>{@link #PAST} - завершенные бронирования (уже закончились)</li>
 *   <li>{@link #FUTURE} - будущие бронирования (ещё не начались)</li>
 *   <li>{@link #WAITING} - бронирования, ожидающие подтверждения владельца вещи</li>
 *   <li>{@link #REJECTED} - отклоненные бронирования (владелец вещи отклонил запрос)</li>
 * </ul>
 *
 * @see ru.practicum.shareit.booking.BookingService
 * @see ru.practicum.shareit.booking.BookingRepository
 */
public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED
}