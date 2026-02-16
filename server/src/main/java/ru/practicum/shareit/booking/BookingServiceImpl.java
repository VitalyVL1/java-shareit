package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingApproveDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.AccessForbiddenException;
import ru.practicum.shareit.exception.NoContentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableItemException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Реализация сервиса {@link BookingService} для управления бронированиями.
 * <p>
 * Обеспечивает бизнес-логику для работы с бронированиями: создание, подтверждение,
 * получение по различным критериям, проверка прав доступа и валидация бизнес-правил.
 * </p>
 *
 * @see BookingService
 * @see BookingRepository
 * @see UserRepository
 * @see ItemRepository
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    /**
     * Создает новое бронирование.
     * <p>
     * Выполняет следующие проверки:
     * <ul>
     *   <li>Существование пользователя и вещи</li>
     *   <li>Пользователь не является владельцем вещи</li>
     *   <li>Вещь доступна для бронирования</li>
     *   <li>Нет активных бронирований на указанный период</li>
     * </ul>
     * </p>
     *
     * @param bookerId идентификатор пользователя, создающего бронирование
     * @param dto      DTO с данными для создания бронирования
     * @return созданное бронирование в виде DTO
     * @throws NotFoundException если пользователь или вещь не найдены
     * @throws AccessForbiddenException если владелец пытается забронировать свою вещь
     * @throws UnavailableItemException если вещь недоступна или уже забронирована на указанный период
     */
    @Transactional
    @Override
    public BookingResponseDto save(Long bookerId, BookingCreateDto dto) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User", bookerId));

        Item item = itemRepository.findById(dto.itemId())
                .orElseThrow(() -> new NotFoundException("Item", dto.itemId()));

        if (Objects.equals(item.getOwner().getId(), bookerId)) {
            throw new AccessForbiddenException("Owner cannot book their own item", bookerId);
        }

        if (!item.getAvailable()) {
            throw new UnavailableItemException(item.getId(), "Item is not available");
        }

        if (bookingRepository.existsActiveBookingForItem(dto.itemId(), dto.start(), dto.end())) {
            throw new UnavailableItemException(dto.itemId(), "Item already booked for this period");
        }

        Booking booking = BookingMapper.toBooking(booker, item, dto);
        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    /**
     * Находит бронирование по его идентификатору.
     * <p>
     * Доступно только автору бронирования или владельцу вещи.
     * </p>
     *
     * @param bookingId идентификатор бронирования
     * @param userId    идентификатор пользователя, запрашивающего информацию
     * @return найденное бронирование в виде DTO
     * @throws NotFoundException если бронирование не найдено
     * @throws AccessForbiddenException если пользователь не имеет доступа к бронированию
     */
    @Override
    public BookingResponseDto findById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByIdWithRelations(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking", bookingId));

        if (!isUserRelatedToBooking(booking, userId)) {
            throw new AccessForbiddenException("No access to booking " + bookingId, userId);
        }

        return BookingMapper.toBookingResponseDto(booking);
    }

    /**
     * Находит все бронирования пользователя (арендатора) с фильтрацией по статусу.
     *
     * @param bookerId идентификатор пользователя-арендатора
     * @param state    статус для фильтрации
     * @return список бронирований пользователя
     * @throws NotFoundException если пользователь не найден
     * @throws NoContentException если у пользователя нет бронирований
     * @throws IllegalArgumentException если передан неизвестный статус
     */
    @Override
    public List<BookingResponseDto> findByBookerIdAndState(Long bookerId, State state) {
        if (!userRepository.existsById(bookerId)) {
            throw new NotFoundException("User", bookerId);
        }

        if (!bookingRepository.existsByBooker_Id(bookerId)) {
            throw new NoContentException("Booking");
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL -> {
                return BookingMapper.toBookingResponseDto(
                        bookingRepository.findAllByBooker_Id(bookerId, sort));
            }
            case PAST -> {
                return BookingMapper.toBookingResponseDto(
                        bookingRepository.findAllByBooker_IdAndEndBefore(bookerId, now, sort));
            }
            case FUTURE -> {
                return BookingMapper.toBookingResponseDto(
                        bookingRepository.findAllByBooker_IdAndStartAfter(bookerId, now, sort));
            }
            case CURRENT -> {
                return BookingMapper.toBookingResponseDto(
                        bookingRepository.findAllCurrentBookingsByBooker(bookerId, now, sort));
            }
            case WAITING, REJECTED -> {
                return BookingMapper.toBookingResponseDto(
                        bookingRepository.findAllByBooker_IdAndStatus(bookerId, Status.valueOf(state.name()), sort));
            }
            default -> throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

    /**
     * Находит все бронирования для вещей владельца с фильтрацией по статусу.
     *
     * @param ownerId идентификатор владельца вещей
     * @param state   статус для фильтрации
     * @return список бронирований для вещей владельца
     * @throws NotFoundException если пользователь не найден
     * @throws NoContentException если для вещей владельца нет бронирований
     * @throws IllegalArgumentException если передан неизвестный статус
     */
    @Override
    public List<BookingResponseDto> findByOwnerIdAndState(Long ownerId, State state) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("User", ownerId);
        }

        if (!bookingRepository.existsByItem_Owner_Id(ownerId)) {
            throw new NoContentException("Booking");
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL -> {
                return BookingMapper.toBookingResponseDto(
                        bookingRepository.findAllByItem_Owner_Id(ownerId, sort));
            }
            case PAST -> {
                return BookingMapper.toBookingResponseDto(
                        bookingRepository.findAllByItem_Owner_IdAndEndBefore(ownerId, now, sort));
            }
            case FUTURE -> {
                return BookingMapper.toBookingResponseDto(
                        bookingRepository.findAllByItem_Owner_IdAndStartAfter(ownerId, now, sort));
            }
            case CURRENT -> {
                return BookingMapper.toBookingResponseDto(
                        bookingRepository.findAllCurrentBookingsByOwner(ownerId, now, sort));
            }
            case WAITING, REJECTED -> {
                return BookingMapper.toBookingResponseDto(
                        bookingRepository.findAllByItem_Owner_IdAndStatus(ownerId, Status.valueOf(state.name()), sort));
            }
            default -> throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

    /**
     * Подтверждает или отклоняет бронирование.
     * <p>
     * Доступно только владельцу вещи. Статус можно изменить только у бронирований
     * в статусе {@link Status#WAITING}.
     * </p>
     *
     * @param dto DTO с идентификатором бронирования, идентификатором владельца и флагом подтверждения
     * @return обновленное бронирование в виде DTO
     * @throws NotFoundException если бронирование не найдено
     * @throws AccessForbiddenException если пользователь не является владельцем вещи
     * @throws IllegalStateException если статус бронирования не WAITING
     */
    @Transactional
    @Override
    public BookingResponseDto approve(BookingApproveDto dto) {
        Booking booking = bookingRepository.findByIdWithItemAndOwner(dto.id())
                .orElseThrow(() -> new NotFoundException("Booking", dto.id()));

        if (!Objects.equals(booking.getItem().getOwner().getId(), dto.ownerId())) {
            throw new AccessForbiddenException("Forbidden to change booking for item not owned by user", dto.ownerId());
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new IllegalStateException("Booking status cannot be changed from " + booking.getStatus());
        }

        booking.setStatus(dto.isApproved() ? Status.APPROVED : Status.REJECTED);

        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    /**
     * Удаляет бронирование по его идентификатору.
     *
     * @param id идентификатор бронирования для удаления
     */
    @Transactional
    @Override
    public void deleteById(Long id) {
        bookingRepository.deleteById(id);
    }

    /**
     * Очищает все бронирования из хранилища.
     * <p>
     * Используется в основном для тестирования.
     * </p>
     */
    @Transactional
    @Override
    public void clear() {
        bookingRepository.deleteAll();
    }

    /**
     * Проверяет, связан ли пользователь с бронированием.
     * <p>
     * Пользователь связан с бронированием, если он является арендатором или владельцем вещи.
     * </p>
     *
     * @param booking бронирование
     * @param userId  идентификатор пользователя
     * @return {@code true}, если пользователь связан с бронированием, иначе {@code false}
     */
    private boolean isUserRelatedToBooking(Booking booking, Long userId) {
        return booking.getBooker().getId().equals(userId)
               || booking.getItem().getOwner().getId().equals(userId);
    }
}