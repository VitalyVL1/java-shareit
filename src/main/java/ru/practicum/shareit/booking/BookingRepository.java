package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdAndStatus(long bookingId, Status status, Sort sort);

    List<Booking> findAllByBooker_Id(long bookingId, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStatus(long ownerId, Status status, Sort sort);

    List<Booking> findAllByItem_Owner_Id(long ownerId, Sort sort);

    List<Booking> findAllByBooker_IdAndItem_Id(long bookingId, long itemId, Sort sort);

    List<Booking> findAllByItem_IdAndStatus(long itemId, Status status);
}
