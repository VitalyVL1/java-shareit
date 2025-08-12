package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdAndStatus(long bookingId, String status, Sort sort);

    List<Booking> findAllByBooker_Id(long bookingId, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStatus(long ownerId, String status, Sort sort);

    List<Booking> findAllByItem_Owner_Id(long ownerId, Sort sort);
}
