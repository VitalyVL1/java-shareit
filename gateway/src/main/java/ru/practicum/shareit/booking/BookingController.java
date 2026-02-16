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

/**
 * Контроллер для обработки HTTP-запросов, связанных с бронированиями, в модуле gateway.
 * <p>
 * Выполняет первичную валидацию входящих данных и перенаправляет запросы
 * в соответствующие методы клиента {@link BookingClient}.
 * </p>
 *
 * @see BookingClient
 * @see BookingCreateDto
 * @see State
 */
@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    /**
     * Создает новый запрос на бронирование вещи.
     * <p>
     * HTTP метод: POST /bookings
     * </p>
     *
     * @param userId     идентификатор пользователя, создающего бронирование (из заголовка X-Sharer-User-Id)
     * @param createDto  DTO с данными для создания бронирования (id вещи, даты начала и окончания)
     * @return {@link ResponseEntity} с результатом операции от сервера
     */
    @PostMapping
    public ResponseEntity<Object> addBooking(
            @RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId,
            @RequestBody @Valid BookingCreateDto createDto) {
        log.info("Creating booking {}, userId={}", createDto, userId);
        return bookingClient.bookItem(userId, createDto);
    }

    /**
     * Подтверждает или отклоняет запрос на бронирование (для владельца вещи).
     * <p>
     * HTTP метод: PATCH /bookings/{bookingId}?approved={approved}
     * </p>
     *
     * @param bookingId  идентификатор бронирования (из пути запроса)
     * @param ownerId    идентификатор владельца вещи (из заголовка X-Sharer-User-Id)
     * @param approved   true - подтвердить бронирование, false - отклонить (из query-параметра)
     * @return {@link ResponseEntity} с обновленным бронированием
     */
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @PathVariable @Positive Long bookingId,
            @RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long ownerId,
            @RequestParam("approved") @NotNull Boolean approved) {
        log.info("Updating a booking for {} by owner {}", bookingId, ownerId);
        return bookingClient.approveBooking(ownerId, bookingId, approved);
    }

    /**
     * Получает информацию о конкретном бронировании по его идентификатору.
     * <p>
     * HTTP метод: GET /bookings/{bookingId}
     * </p>
     *
     * @param userId     идентификатор пользователя (должен быть либо автором, либо владельцем)
     * @param bookingId  идентификатор бронирования (из пути запроса)
     * @return {@link ResponseEntity} с данными бронирования
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId,
            @PathVariable @Positive Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    /**
     * Получает список бронирований для конкретного пользователя (который бронирует вещи)
     * с возможностью фильтрации по статусу и пагинации.
     * <p>
     * HTTP метод: GET /bookings?state={state}&from={from}&size={size}
     * </p>
     *
     * @param bookerId    идентификатор пользователя-арендатора (из заголовка X-Sharer-User-Id)
     * @param stateParam  строковое представление статуса для фильтрации (по умолчанию "all")
     * @param from        индекс первого элемента для пагинации (по умолчанию 0)
     * @param size        количество элементов на странице (по умолчанию 10)
     * @return {@link ResponseEntity} со списком бронирований
     * @throws IllegalArgumentException если передан неизвестный статус в stateParam
     */
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

    /**
     * Получает список бронирований для всех вещей конкретного владельца
     * с возможностью фильтрации по статусу и пагинации.
     * <p>
     * HTTP метод: GET /bookings/owner?state={state}&from={from}&size={size}
     * </p>
     *
     * @param ownerId     идентификатор владельца вещей (из заголовка X-Sharer-User-Id)
     * @param stateParam  строковое представление статуса для фильтрации (по умолчанию "all")
     * @param from        индекс первого элемента для пагинации (по умолчанию 0)
     * @param size        количество элементов на странице (по умолчанию 10)
     * @return {@link ResponseEntity} со списком бронирований для вещей владельца
     * @throws IllegalArgumentException если передан неизвестный статус в stateParam
     */
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