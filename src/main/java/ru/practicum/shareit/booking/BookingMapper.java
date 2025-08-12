package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

public class BookingMapper {
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

    public static List<BookingResponseDto> toBookingResponseDto(List<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) return Collections.emptyList();

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .toList();
    }

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
