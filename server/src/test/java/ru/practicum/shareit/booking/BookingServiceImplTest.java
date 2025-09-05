package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(BookingServiceImpl.class)
class BookingServiceImplTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User createUser(String name, String email) {
        User user = User.builder()
                .name(name)
                .email(email)
                .build();
        return userRepository.save(user);
    }

    private Item createItem(User owner, String name, String description, Boolean available) {
        Item item = Item.builder()
                .name(name)
                .description(description)
                .available(available)
                .owner(owner)
                .build();
        return itemRepository.save(item);
    }

    private Booking createBooking(User booker, Item item, LocalDateTime start, LocalDateTime end, Status status) {
        Booking booking = Booking.builder()
                .booker(booker)
                .item(item)
                .start(start)
                .end(end)
                .status(status)
                .build();
        return bookingRepository.save(booking);
    }

    @Test
    void save_ShouldCreateBooking() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingCreateDto createDto = new BookingCreateDto(item.getId(), start, end);

        BookingResponseDto savedBooking = bookingService.save(booker.getId(), createDto);

        assertNotNull(savedBooking.id());
        assertEquals(Status.WAITING, savedBooking.status());
        assertEquals(booker.getId(), savedBooking.booker().id());
        assertEquals(item.getId(), savedBooking.item().id());
        assertEquals(start, savedBooking.start());
        assertEquals(end, savedBooking.end());
    }

    @Test
    void save_ShouldThrowNotFoundException_WhenUserNotExists() {
        User owner = createUser("Owner", "owner@email.com");
        Item item = createItem(owner, "Drill", "Powerful drill", true);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingCreateDto createDto = new BookingCreateDto(item.getId(), start, end);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.save(999L, createDto));

        assertEquals("User", exception.getEntityName());
        assertEquals(999L, exception.getEntityId());
    }

    @Test
    void save_ShouldThrowNotFoundException_WhenItemNotExists() {
        User booker = createUser("Booker", "booker@email.com");

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingCreateDto createDto = new BookingCreateDto(999L, start, end);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.save(booker.getId(), createDto));

        assertEquals("Item", exception.getEntityName());
        assertEquals(999L, exception.getEntityId());
    }

    @Test
    void save_ShouldThrowAccessForbiddenException_WhenOwnerBooksOwnItem() {
        User owner = createUser("Owner", "owner@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingCreateDto createDto = new BookingCreateDto(item.getId(), start, end);

        // When & Then
        AccessForbiddenException exception = assertThrows(AccessForbiddenException.class,
                () -> bookingService.save(owner.getId(), createDto));

        assertTrue(exception.getMessage().contains("Owner cannot book their own item"));
    }

    @Test
    void save_ShouldThrowUnavailableItemException_WhenItemNotAvailable() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", false);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingCreateDto createDto = new BookingCreateDto(item.getId(), start, end);

        UnavailableItemException exception = assertThrows(UnavailableItemException.class,
                () -> bookingService.save(booker.getId(), createDto));

        assertEquals(item.getId(), exception.getItemId());
        assertTrue(exception.getMessage().contains("not available"));
    }

    @Test
    void save_ShouldThrowUnavailableItemException_WhenItemAlreadyBooked() {
        User owner = createUser("Owner", "owner@email.com");
        User booker1 = createUser("Booker1", "booker1@email.com");
        User booker2 = createUser("Booker2", "booker2@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);

        createBooking(booker1, item, start, end, Status.APPROVED);

        LocalDateTime overlappingStart = LocalDateTime.now().plusDays(2);
        LocalDateTime overlappingEnd = LocalDateTime.now().plusDays(4);
        BookingCreateDto createDto = new BookingCreateDto(item.getId(), overlappingStart, overlappingEnd);

        UnavailableItemException exception = assertThrows(UnavailableItemException.class,
                () -> bookingService.save(booker2.getId(), createDto));

        assertEquals(item.getId(), exception.getItemId());
        assertTrue(exception.getMessage().contains("already booked"));
    }

    @Test
    void findById_ShouldReturnBookingForBooker() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = createBooking(booker, item, start, end, Status.WAITING);

        BookingResponseDto foundBooking = bookingService.findById(booking.getId(), booker.getId());

        assertNotNull(foundBooking);
        assertEquals(booking.getId(), foundBooking.id());
        assertEquals(booker.getId(), foundBooking.booker().id());
    }

    @Test
    void findById_ShouldReturnBookingForOwner() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = createBooking(booker, item, start, end, Status.WAITING);

        BookingResponseDto foundBooking = bookingService.findById(booking.getId(), owner.getId());

        assertNotNull(foundBooking);
        assertEquals(booking.getId(), foundBooking.id());
        assertEquals(item.getId(), foundBooking.item().id());
    }

    @Test
    void findById_ShouldThrowAccessForbiddenException_ForUnauthorizedUser() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        User otherUser = createUser("Other", "other@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = createBooking(booker, item, start, end, Status.WAITING);

        // When & Then
        AccessForbiddenException exception = assertThrows(AccessForbiddenException.class,
                () -> bookingService.findById(booking.getId(), otherUser.getId()));

        assertTrue(exception.getMessage().contains("No access to booking"));
    }

    @Test
    void findByBookerIdAndState_ShouldReturnAllBookings() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime now = LocalDateTime.now();
        Booking pastBooking = createBooking(booker, item, now.minusDays(3), now.minusDays(2), Status.APPROVED);
        Booking futureBooking = createBooking(booker, item, now.plusDays(1), now.plusDays(2), Status.WAITING);

        List<BookingResponseDto> bookings = bookingService.findByBookerIdAndState(booker.getId(), State.ALL);

        assertEquals(2, bookings.size());
        assertTrue(bookings.stream().anyMatch(b -> b.id().equals(pastBooking.getId())));
        assertTrue(bookings.stream().anyMatch(b -> b.id().equals(futureBooking.getId())));
    }

    @Test
    void findByBookerIdAndState_ShouldReturnPastBookings() {
        // Given
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime now = LocalDateTime.now();
        Booking pastBooking = createBooking(booker, item, now.minusDays(3), now.minusDays(2), Status.APPROVED);
        createBooking(booker, item, now.plusDays(1), now.plusDays(2), Status.WAITING);

        List<BookingResponseDto> pastBookings = bookingService.findByBookerIdAndState(booker.getId(), State.PAST);

        assertEquals(1, pastBookings.size());
        assertEquals(pastBooking.getId(), pastBookings.get(0).id());
    }

    @Test
    void findByBookerIdAndState_ShouldReturnFutureBookings() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime now = LocalDateTime.now();
        createBooking(booker, item, now.minusDays(3), now.minusDays(2), Status.APPROVED);
        Booking futureBooking = createBooking(booker, item, now.plusDays(1), now.plusDays(2), Status.WAITING);

        List<BookingResponseDto> futureBookings = bookingService.findByBookerIdAndState(booker.getId(), State.FUTURE);

        assertEquals(1, futureBookings.size());
        assertEquals(futureBooking.getId(), futureBookings.get(0).id());
    }

    @Test
    void findByBookerIdAndState_ShouldReturnCurrentBookings() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime now = LocalDateTime.now();
        createBooking(booker, item, now.minusDays(3), now.minusDays(2), Status.APPROVED);
        createBooking(booker, item, now.plusDays(1), now.plusDays(2), Status.WAITING);

        Booking currentBooking = createBooking(booker, item, now.minusHours(1), now.plusHours(1), Status.APPROVED);

        List<BookingResponseDto> currentBookings = bookingService.findByBookerIdAndState(booker.getId(), State.CURRENT);

        assertEquals(1, currentBookings.size());
        assertEquals(currentBooking.getId(), currentBookings.get(0).id());
    }

    @Test
    void findByBookerIdAndState_ShouldReturnWaitingBookings() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime now = LocalDateTime.now();
        createBooking(booker, item, now.plusDays(1), now.plusDays(2), Status.APPROVED);
        Booking waitingBooking = createBooking(booker, item, now.plusDays(3), now.plusDays(4), Status.WAITING);

        List<BookingResponseDto> waitingBookings = bookingService.findByBookerIdAndState(booker.getId(), State.WAITING);

        assertEquals(1, waitingBookings.size());
        assertEquals(waitingBooking.getId(), waitingBookings.get(0).id());
        assertEquals(Status.WAITING, waitingBookings.get(0).status());
    }

    @Test
    void findByBookerIdAndState_ShouldReturnRejectedBookings() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime now = LocalDateTime.now();
        createBooking(booker, item, now.plusDays(1), now.plusDays(2), Status.APPROVED);
        Booking rejectedBooking = createBooking(booker, item, now.plusDays(3), now.plusDays(4), Status.REJECTED);

        List<BookingResponseDto> rejectedBookings = bookingService.findByBookerIdAndState(booker.getId(), State.REJECTED);

        assertEquals(1, rejectedBookings.size());
        assertEquals(rejectedBooking.getId(), rejectedBookings.get(0).id());
        assertEquals(Status.REJECTED, rejectedBookings.get(0).status());
    }

    @Test
    void findByBookerIdAndState_ShouldThrowNoContentException_WhenNoBookings() {
        User booker = createUser("Booker", "booker@email.com");

        NoContentException exception = assertThrows(NoContentException.class,
                () -> bookingService.findByBookerIdAndState(booker.getId(), State.ALL));

        assertEquals("Booking", exception.getContentType());
    }

    @Test
    void findByBookerIdAndState_ShouldThrowNotFoundException_WhenNoUser() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.findByBookerIdAndState(999L, State.ALL));

        assertEquals("User", exception.getEntityName());
        assertEquals(999, exception.getEntityId());
    }

    @Test
    void findByOwnerIdAndState_ShouldReturnAllBookingsForOwner() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime now = LocalDateTime.now();
        Booking pastBooking = createBooking(booker, item, now.minusDays(3), now.minusDays(2), Status.APPROVED);
        Booking futureBooking = createBooking(booker, item, now.plusDays(1), now.plusDays(2), Status.WAITING);

        List<BookingResponseDto> bookings = bookingService.findByOwnerIdAndState(owner.getId(), State.ALL);

        assertEquals(2, bookings.size());
        assertTrue(bookings.stream().allMatch(b -> b.item().id().equals(item.getId())));
    }

    @Test
    void findByOwnerIdAndState_ShouldReturnPastBookings() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime now = LocalDateTime.now();
        createBooking(booker, item, now.plusDays(1), now.plusDays(2), Status.APPROVED);
        Booking pastBooking = createBooking(booker, item, now.minusDays(2), now.minusDays(1), Status.APPROVED);

        List<BookingResponseDto> pastBookings = bookingService.findByOwnerIdAndState(owner.getId(), State.PAST);

        assertEquals(1, pastBookings.size());
        assertEquals(pastBooking.getId(), pastBookings.get(0).id());
    }

    @Test
    void findByOwnerIdAndState_ShouldReturnFutureBookings() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime now = LocalDateTime.now();
        createBooking(booker, item, now.minusDays(2), now.minusDays(1), Status.APPROVED);
        Booking futureBooking = createBooking(booker, item, now.plusDays(3), now.plusDays(4), Status.APPROVED);

        List<BookingResponseDto> futureBookings = bookingService.findByOwnerIdAndState(owner.getId(), State.FUTURE);

        assertEquals(1, futureBookings.size());
        assertEquals(futureBooking.getId(), futureBookings.get(0).id());
    }

    @Test
    void findByOwnerIdAndState_ShouldReturnCurrentBookings() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime now = LocalDateTime.now();
        createBooking(booker, item, now.minusDays(2), now.minusDays(1), Status.APPROVED);
        createBooking(booker, item, now.plusDays(2), now.plusDays(3), Status.APPROVED);
        Booking currentBooking = createBooking(booker, item, now.minusHours(1), now.plusHours(1), Status.APPROVED);

        List<BookingResponseDto> currentBookings = bookingService.findByOwnerIdAndState(owner.getId(), State.CURRENT);

        assertEquals(1, currentBookings.size());
        assertEquals(currentBooking.getId(), currentBookings.get(0).id());
    }

    @Test
    void findByOwnerIdAndState_ShouldReturnWaitingBookings() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime now = LocalDateTime.now();
        createBooking(booker, item, now.plusDays(1), now.plusDays(2), Status.APPROVED);
        Booking waitingBooking = createBooking(booker, item, now.plusDays(3), now.plusDays(4), Status.WAITING);

        List<BookingResponseDto> waitingBookings = bookingService.findByOwnerIdAndState(owner.getId(), State.WAITING);

        assertEquals(1, waitingBookings.size());
        assertEquals(waitingBooking.getId(), waitingBookings.get(0).id());
        assertEquals(Status.WAITING, waitingBookings.get(0).status());
    }

    @Test
    void findByOwnerIdAndState_ShouldReturnRejectedBookings() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime now = LocalDateTime.now();
        createBooking(booker, item, now.plusDays(1), now.plusDays(2), Status.APPROVED);
        Booking rejectedBooking = createBooking(booker, item, now.plusDays(3), now.plusDays(4), Status.REJECTED);

        List<BookingResponseDto> rejectedBookings = bookingService.findByOwnerIdAndState(owner.getId(), State.REJECTED);

        assertEquals(1, rejectedBookings.size());
        assertEquals(rejectedBooking.getId(), rejectedBookings.get(0).id());
        assertEquals(Status.REJECTED, rejectedBookings.get(0).status());
    }

    @Test
    void findByOwnerIdAndState_ShouldThrowNoContentException_WhenNoBookings() {
        User owner = createUser("Booker", "booker@email.com");

        NoContentException exception = assertThrows(NoContentException.class,
                () -> bookingService.findByOwnerIdAndState(owner.getId(), State.ALL));

        assertEquals("Booking", exception.getContentType());
    }

    @Test
    void findByOwnerIdAndState_ShouldThrowNotFoundException_WhenNoUser() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.findByOwnerIdAndState(999L, State.ALL));

        assertEquals("User", exception.getEntityName());
        assertEquals(999, exception.getEntityId());
    }


    @Test
    void approve_ShouldApproveBooking() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = createBooking(booker, item, start, end, Status.WAITING);

        BookingApproveDto approveDto = new BookingApproveDto(booking.getId(), owner.getId(), item.getId(), true);

        BookingResponseDto approvedBooking = bookingService.approve(approveDto);

        assertEquals(Status.APPROVED, approvedBooking.status());
        assertEquals(booking.getId(), approvedBooking.id());
    }

    @Test
    void approve_ShouldRejectBooking() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = createBooking(booker, item, start, end, Status.WAITING);

        BookingApproveDto approveDto = new BookingApproveDto(booking.getId(), owner.getId(), item.getId(), false);

        BookingResponseDto rejectedBooking = bookingService.approve(approveDto);

        assertEquals(Status.REJECTED, rejectedBooking.status());
        assertEquals(booking.getId(), rejectedBooking.id());
    }

    @Test
    void approve_ShouldThrowAccessForbiddenException_WhenNotOwner() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        User otherUser = createUser("Other", "other@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = createBooking(booker, item, start, end, Status.WAITING);

        BookingApproveDto approveDto = new BookingApproveDto(booking.getId(), otherUser.getId(), item.getId(), true);

        AccessForbiddenException exception = assertThrows(AccessForbiddenException.class,
                () -> bookingService.approve(approveDto));

        assertTrue(exception.getMessage().contains("Forbidden to change booking for item not owned by user"));
    }

    @Test
    void approve_ShouldThrowIllegalStateException_WhenStatusNotWaiting() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = createBooking(booker, item, start, end, Status.APPROVED);

        BookingApproveDto approveDto = new BookingApproveDto(booking.getId(), owner.getId(), item.getId(), true);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> bookingService.approve(approveDto));

        assertTrue(exception.getMessage().contains("cannot be changed from"));
    }

    @Test
    void deleteById_ShouldRemoveBooking() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = createBooking(booker, item, start, end, Status.WAITING);

        assertNotNull(bookingService.findById(booking.getId(), booker.getId()));

        bookingService.deleteById(booking.getId());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.findById(booking.getId(), booker.getId()));

        assertEquals("Booking", exception.getEntityName());
        assertEquals(booking.getId(), exception.getEntityId());
    }

    @Test
    void clear_ShouldRemoveAllBookings() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true);

        LocalDateTime now = LocalDateTime.now();
        createBooking(booker, item, now.plusDays(1), now.plusDays(2), Status.WAITING);
        createBooking(booker, item, now.plusDays(3), now.plusDays(4), Status.APPROVED);

        assertEquals(2, bookingService.findByBookerIdAndState(booker.getId(), State.ALL).size());

        bookingService.clear();

        NoContentException exception = assertThrows(NoContentException.class,
                () -> bookingService.findByBookerIdAndState(booker.getId(), State.ALL));

        assertEquals("Booking", exception.getContentType());
    }
}