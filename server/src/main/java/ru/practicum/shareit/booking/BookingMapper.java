package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

/**
 * Утилитарный класс для преобразования между сущностью {@link Booking} и DTO.
 * <p>
 * Содержит статические методы для преобразования объектов бронирования в различные
 * форматы DTO и обратно. Используется в сервисном слое для изоляции логики
 * преобразования и предотвращения циклических зависимостей.
 * </p>
 *
 * @see Booking
 * @see BookingResponseDto
 * @see BookingCreateDto
 */
public class BookingMapper {

    /**
     * Преобразует сущность {@link Booking} в {@link BookingResponseDto}.
     * <p>
     * Использует {@link ItemMapper#toItemShortDto} и {@link UserMapper#toUserResponseDto}
     * для преобразования связанных сущностей.
     * </p>
     *
     * @param booking сущность бронирования (может быть {@code null})
     * @return DTO с полной информацией о бронировании или {@code null}, если входной параметр равен {@code null}
     */
    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        if (booking == null) return null;

        return BookingResponseDto.builder()
                .id(booking.getId())
                .item(ItemMapper.toItemShortDto(booking.getItem()))
                .booker(UserMapper.toUserResponseDto(booking.getBooker()))
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    /**
     * Преобразует список сущностей {@link Booking} в список {@link BookingResponseDto}.
     * <p>
     * Применяет {@link #toBookingResponseDto(Booking)} к каждому элементу списка.
     * </p>
     *
     * @param bookings список сущностей бронирований (может быть {@code null} или пустым)
     * @return список DTO с информацией о бронированиях или пустой список,
     *         если входной параметр равен {@code null} или пуст
     */
    public static List<BookingResponseDto> toBookingResponseDto(List<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) return Collections.emptyList();

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .toList();
    }

    /**
     * Создает сущность {@link Booking} из DTO создания и связанных сущностей.
     * <p>
     * Используется при создании нового бронирования. Статус бронирования не устанавливается,
     * так как по умолчанию будет {@link ru.practicum.shareit.booking.model.Status#WAITING}.
     * </p>
     *
     * @param booker пользователь, создающий бронирование (арендатор)
     * @param item   вещь, которую бронируют
     * @param dto    DTO с датами начала и окончания бронирования
     * @return новая сущность бронирования или {@code null}, если DTO равен {@code null}
     */
    public static Booking toBooking(User booker, Item item, BookingCreateDto dto) {
        if (dto == null) return null;

        return Booking.builder()
                .start(dto.start())
                .end(dto.end())
                .item(item)
                .booker(booker)
                .build();
    }
}