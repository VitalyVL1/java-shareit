package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAprovedDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto addBooking(
            @Valid @RequestBody BookingCreateDto dto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Adding a booking for item {} by booker {}", dto, userId);
        return bookingService.save(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto aproveBooking(
            @PathVariable @Valid @NonNull @Positive Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam("approved") Boolean approved) {
        log.info("Updating a booking for {} by owner {}", bookingId, ownerId);
        BookingAprovedDto dto = BookingAprovedDto.builder()
                .id(bookingId)
                .ownerId(ownerId)
                .isApproved(approved)
                .build();
        return bookingService.approve(dto);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto getBookingById(
            @PathVariable @Valid @NonNull @Positive Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Getting a booking for {} by user {}", bookingId, userId);
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getBookingsByBookerAndState(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") State state
    ) {
        log.info("Getting bookings by ownerId {} and state {}", bookerId, state);
        return bookingService.findByBookerIdAndState(bookerId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getBookingsByOwnerAndState(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") State state
    ) {
        log.info("Getting bookings by ownerId {} and state {}", ownerId, state);
        return bookingService.findByOwnerIdAndState(ownerId, state);
    }
}
