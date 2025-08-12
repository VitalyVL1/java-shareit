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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableItemException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

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
        if (!userRepository.existsById(bookerId)) {
            throw new NotFoundException("User", bookerId);
        }

        Item item = itemRepository.findById(dto.itemId())
                .orElseThrow(() -> new NotFoundException("Item", dto.itemId()));

        if (!item.getAvailable()) {
            throw new UnavailableItemException(item.getId(), "Item is not available");
        }

        if (bookingRepository.existsActiveBookingForItem(dto.itemId(), dto.start(), dto.end())) {
            throw new UnavailableItemException(dto.itemId(), "Item already booked for this period");
        }

        Booking booking = BookingMapper.toBooking(userRepository.getReferenceById(bookerId), item, dto);
        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto findById(Long id) {
        return bookingRepository.findById(id)
                .map(BookingMapper::toBookingResponseDto)
                .orElseThrow(() -> new NotFoundException("Booking", id));
    }

    @Override
    public List<BookingResponseDto> findByBookerIdAndState(Long bookerId, State state) {
        getUserById(bookerId); // проверка на существование пользователя

        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        if (state.equals(State.ALL)) {
            return BookingMapper.toBookingResponseDto(
                    bookingRepository.findAllByBooker_Id(bookerId, sort));
        }

        return BookingMapper.toBookingResponseDto(
                bookingRepository.findAllByBooker_IdAndStatus(bookerId, Status.valueOf(state.name()), sort));
    }

    @Override
    public List<BookingResponseDto> findByOwnerIdAndState(Long ownerId, State state) {
        getUserById(ownerId); // проверка на существование пользователя

        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        if (state.equals(State.ALL)) {
            return BookingMapper.toBookingResponseDto(
                    bookingRepository.findAllByItem_Owner_Id(ownerId, sort));
        }

        return BookingMapper.toBookingResponseDto(
                bookingRepository.findAllByItem_Owner_IdAndStatus(ownerId, Status.valueOf(state.name()), sort));
    }

    @Transactional
    @Override
    public BookingResponseDto approve(BookingAprovedDto dto) {
        Booking booking = bookingRepository.findById(dto.id())
                .orElseThrow(() -> new NotFoundException("Booking", dto.id()));

        if (!Objects.equals(booking.getItem().getOwner().getId(), dto.ownerId())) {
            throw new AccessForbiddenException("Forbidden to change booking for item not owned by user", dto.ownerId());
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

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item", itemId));
    }
}
