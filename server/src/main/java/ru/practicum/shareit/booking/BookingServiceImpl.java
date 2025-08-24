package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingAprovedDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

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

    @Override
    public BookingResponseDto findById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByIdWithRelations(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking", bookingId));

        if (!isUserRelatedToBooking(booking, userId)) {
            throw new AccessForbiddenException("No access to booking " + bookingId, userId);
        }

        return BookingMapper.toBookingResponseDto(booking);
    }

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

    @Transactional
    @Override
    public BookingResponseDto approve(BookingAprovedDto dto) {
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

    @Transactional
    @Override
    public void deleteById(Long id) {
        bookingRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void clear() {
        bookingRepository.deleteAll();
    }

    private boolean isUserRelatedToBooking(Booking booking, Long userId) {
        return booking.getBooker().getId().equals(userId)
               || booking.getItem().getOwner().getId().equals(userId);
    }
}
