package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingAprovedDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingResponseDto save(Long bookerId, BookingCreateDto dto);

    BookingResponseDto findById(Long bookingId, Long userId);

    List<BookingResponseDto> findByBookerIdAndState(Long bookerId, State state);

    List<BookingResponseDto> findByOwnerIdAndState(Long ownerId, State state);

    BookingResponseDto approve(BookingAprovedDto dto);

    void deleteById(Long id);

    void clear();
}
