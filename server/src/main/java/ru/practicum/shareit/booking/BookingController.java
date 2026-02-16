package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingApproveDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.State;

import java.util.List;

/**
 * REST-контроллер для управления бронированиями в модуле server.
 * <p>
 * Предоставляет endpoints для создания, подтверждения, получения и фильтрации бронирований.
 * Все запросы уже прошли первичную валидацию в gateway, поэтому здесь валидация минимальна.
 * </p>
 *
 * @see BookingService
 * @see BookingCreateDto
 * @see BookingResponseDto
 * @see State
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    /**
     * Создает новое бронирование.
     * <p>
     * HTTP метод: POST /bookings
     * </p>
     *
     * @param dto    DTO с данными для создания бронирования (id вещи, даты начала и окончания)
     * @param userId идентификатор пользователя, создающего бронирование (из заголовка X-Sharer-User-Id)
     * @return созданное бронирование с полной информацией
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto addBooking(
            @RequestBody BookingCreateDto dto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Adding a booking for item {} by booker {}", dto, userId);
        return bookingService.save(userId, dto);
    }

    /**
     * Подтверждает или отклоняет бронирование.
     * <p>
     * HTTP метод: PATCH /bookings/{bookingId}?approved={approved}
     * </p>
     *
     * @param bookingId идентификатор бронирования (из пути запроса)
     * @param ownerId   идентификатор владельца вещи (из заголовка X-Sharer-User-Id)
     * @param approved  флаг подтверждения: true - подтвердить, false - отклонить (из query-параметра)
     * @return обновленное бронирование с измененным статусом
     */
    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto approveBooking(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam("approved") Boolean approved) {
        log.info("Updating a booking for {} by owner {}", bookingId, ownerId);
        BookingApproveDto dto = BookingApproveDto.builder()
                .id(bookingId)
                .ownerId(ownerId)
                .isApproved(approved)
                .build();
        return bookingService.approve(dto);
    }

    /**
     * Получает информацию о конкретном бронировании по его идентификатору.
     * <p>
     * HTTP метод: GET /bookings/{bookingId}
     * </p>
     *
     * @param userId    идентификатор пользователя, запрашивающего информацию (должен быть либо автором, либо владельцем)
     * @param bookingId идентификатор бронирования (из пути запроса)
     * @return полная информация о бронировании
     */
    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto getBookingById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId
    ) {
        log.info("Getting a booking for {} by user {}", bookingId, userId);
        return bookingService.findById(bookingId, userId);
    }

    /**
     * Получает список бронирований для конкретного пользователя (арендатора) с фильтрацией по статусу.
     * <p>
     * HTTP метод: GET /bookings?state={state}&from={from}&size={size}
     * </p>
     *
     * @param bookerId идентификатор пользователя-арендатора (из заголовка X-Sharer-User-Id)
     * @param state    статус для фильтрации (по умолчанию ALL)
     * @param from     индекс первого элемента для пагинации (по умолчанию 0)
     * @param size     количество элементов на странице (по умолчанию 10)
     * @return список бронирований, соответствующих критериям
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getBookingsByBookerAndState(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(value = "state", defaultValue = "ALL") State state,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        log.info("Getting bookings by ownerId {} and state {}", bookerId, state);
        return bookingService.findByBookerIdAndState(bookerId, state);
    }

    /**
     * Получает список бронирований для всех вещей конкретного владельца с фильтрацией по статусу.
     * <p>
     * HTTP метод: GET /bookings/owner?state={state}&from={from}&size={size}
     * </p>
     *
     * @param ownerId идентификатор владельца вещей (из заголовка X-Sharer-User-Id)
     * @param state   статус для фильтрации (по умолчанию ALL)
     * @param from    индекс первого элемента для пагинации (по умолчанию 0)
     * @param size    количество элементов на странице (по умолчанию 10)
     * @return список бронирований для вещей владельца, соответствующих критериям
     */
    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getBookingsByOwnerAndState(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") State state,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        log.info("Getting bookings by ownerId {} and state {}", ownerId, state);
        return bookingService.findByOwnerIdAndState(ownerId, state);
    }
}