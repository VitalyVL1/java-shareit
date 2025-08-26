package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.State;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(
            @RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId,
            @RequestBody @Valid BookingCreateDto createDto) {
        log.info("Creating booking {}, userId={}", createDto, userId);
        return bookingClient.bookItem(userId, createDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @PathVariable @Positive Long bookingId,
            @RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long ownerId,
            @RequestParam("approved") @NotNull Boolean approved) {
        log.info("Updating a booking for {} by owner {}", bookingId, ownerId);
        return bookingClient.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId,
            @PathVariable @Positive Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByBookerAndState(
            @RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long bookerId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, bookerId, from, size);
        return bookingClient.getBookingsByBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwnerAndState(
            @RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long ownerId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Getting bookings by ownerId {} and state {}", ownerId, state);
        return bookingClient.getBookingsByOwner(ownerId, state, from, size);
    }
}
