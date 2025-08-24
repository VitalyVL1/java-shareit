package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdAndStatus(long bookerId, Status status, Sort sort);

    List<Booking> findAllByBooker_Id(long bookerId, Sort sort);

    List<Booking> findAllByBooker_IdAndStartAfter(long bookerId, LocalDateTime currentDateTime, Sort sort);

    List<Booking> findAllByBooker_IdAndEndBefore(long bookerId, LocalDateTime currentDateTime, Sort sort);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = :bookerId
            AND :currentDateTime BETWEEN b.start AND b.end
            """)
    List<Booking> findAllCurrentBookingsByBooker(
            @Param("bookerId") long bookerId,
            @Param("currentDateTime") LocalDateTime currentDateTime,
            Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStatus(long ownerId, Status status, Sort sort);

    List<Booking> findAllByItem_Owner_Id(long ownerId, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStartAfter(long ownerId, LocalDateTime currentDateTime, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndEndBefore(long ownerId, LocalDateTime currentDateTime, Sort sort);

    @Query("""
            SELECT b FROM Booking b
            JOIN FETCH b.item i
            WHERE i.owner.id = :ownerId
            AND :currentDateTime BETWEEN b.start AND b.end
            """)
    List<Booking> findAllCurrentBookingsByOwner(
            @Param("ownerId") long ownerId,
            @Param("currentDateTime") LocalDateTime currentDateTime,
            Sort sort);

    List<Booking> findAllByBooker_IdAndItem_Id(long bookingId, long itemId, Sort sort);

    List<Booking> findAllByItem_IdAndStatus(long itemId, Status status);

    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.item.id = :itemId AND b.status = 'APPROVED'
            AND NOT (b.end <= :start OR b.start >= :end)
            """)
    boolean existsActiveBookingForItem(
            @Param("itemId") Long itemId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    boolean existsByBooker_Id(long bookerId);

    boolean existsByItem_Owner_Id(long ownerId);

    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.booker " +
           "LEFT JOIN FETCH b.item " +
           "LEFT JOIN FETCH b.item.owner " +
           "WHERE b.id = :id")
    Optional<Booking> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.item " +
           "LEFT JOIN FETCH b.item.owner " +
           "WHERE b.id = :id")
    Optional<Booking> findByIdWithItemAndOwner(@Param("id") Long id);
}
