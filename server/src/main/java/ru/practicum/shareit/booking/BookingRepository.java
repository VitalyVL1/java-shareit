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

/**
 * Репозиторий для управления сущностями {@link Booking} в базе данных.
 * <p>
 * Предоставляет методы для выполнения операций с бронированиями, включая
 * поиск по различным критериям (пользователь, владелец, статус, даты),
 * проверку существования бронирований и загрузку связанных сущностей.
 * </p>
 *
 * @see Booking
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Находит все бронирования пользователя с указанным статусом.
     *
     * @param bookerId идентификатор пользователя-арендатора
     * @param status   статус бронирования
     * @param sort     параметры сортировки
     * @return список бронирований пользователя с указанным статусом
     */
    List<Booking> findAllByBooker_IdAndStatus(long bookerId, Status status, Sort sort);

    /**
     * Находит все бронирования пользователя.
     *
     * @param bookerId идентификатор пользователя-арендатора
     * @param sort     параметры сортировки
     * @return список всех бронирований пользователя
     */
    List<Booking> findAllByBooker_Id(long bookerId, Sort sort);

    /**
     * Находит все будущие бронирования пользователя (с датой начала после указанной).
     *
     * @param bookerId         идентификатор пользователя-арендатора
     * @param currentDateTime текущая дата и время
     * @param sort            параметры сортировки
     * @return список будущих бронирований пользователя
     */
    List<Booking> findAllByBooker_IdAndStartAfter(long bookerId, LocalDateTime currentDateTime, Sort sort);

    /**
     * Находит все завершенные бронирования пользователя (с датой окончания до указанной).
     *
     * @param bookerId         идентификатор пользователя-арендатора
     * @param currentDateTime текущая дата и время
     * @param sort            параметры сортировки
     * @return список завершенных бронирований пользователя
     */
    List<Booking> findAllByBooker_IdAndEndBefore(long bookerId, LocalDateTime currentDateTime, Sort sort);

    /**
     * Находит все текущие бронирования пользователя (где текущая дата между start и end).
     *
     * @param bookerId         идентификатор пользователя-арендатора
     * @param currentDateTime текущая дата и время
     * @param sort            параметры сортировки
     * @return список текущих бронирований пользователя
     */
    @Query("""
            SELECT b FROM Booking b
            WHERE b.booker.id = :bookerId
            AND :currentDateTime BETWEEN b.start AND b.end
            """)
    List<Booking> findAllCurrentBookingsByBooker(
            @Param("bookerId") long bookerId,
            @Param("currentDateTime") LocalDateTime currentDateTime,
            Sort sort);

    /**
     * Находит все бронирования для вещей владельца с указанным статусом.
     *
     * @param ownerId идентификатор владельца вещей
     * @param status  статус бронирования
     * @param sort    параметры сортировки
     * @return список бронирований с указанным статусом для вещей владельца
     */
    List<Booking> findAllByItem_Owner_IdAndStatus(long ownerId, Status status, Sort sort);

    /**
     * Находит все бронирования для вещей владельца с указанным статусом (без сортировки).
     *
     * @param ownerId идентификатор владельца вещей
     * @param status  статус бронирования
     * @return список бронирований с указанным статусом для вещей владельца
     */
    List<Booking> findAllByItem_Owner_IdAndStatus(long ownerId, Status status);

    /**
     * Находит все бронирования для всех вещей владельца.
     *
     * @param ownerId идентификатор владельца вещей
     * @param sort    параметры сортировки
     * @return список всех бронирований для вещей владельца
     */
    List<Booking> findAllByItem_Owner_Id(long ownerId, Sort sort);

    /**
     * Находит все будущие бронирования для вещей владельца.
     *
     * @param ownerId          идентификатор владельца вещей
     * @param currentDateTime текущая дата и время
     * @param sort            параметры сортировки
     * @return список будущих бронирований для вещей владельца
     */
    List<Booking> findAllByItem_Owner_IdAndStartAfter(long ownerId, LocalDateTime currentDateTime, Sort sort);

    /**
     * Находит все завершенные бронирования для вещей владельца.
     *
     * @param ownerId          идентификатор владельца вещей
     * @param currentDateTime текущая дата и время
     * @param sort            параметры сортировки
     * @return список завершенных бронирований для вещей владельца
     */
    List<Booking> findAllByItem_Owner_IdAndEndBefore(long ownerId, LocalDateTime currentDateTime, Sort sort);

    /**
     * Находит все текущие бронирования для вещей владельца.
     *
     * @param ownerId          идентификатор владельца вещей
     * @param currentDateTime текущая дата и время
     * @param sort            параметры сортировки
     * @return список текущих бронирований для вещей владельца
     */
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

    /**
     * Находит все бронирования конкретного пользователя для конкретной вещи.
     *
     * @param bookerId идентификатор пользователя-арендатора
     * @param itemId   идентификатор вещи
     * @param sort     параметры сортировки
     * @return список бронирований пользователя для указанной вещи
     */
    List<Booking> findAllByBooker_IdAndItem_Id(long bookerId, long itemId, Sort sort);

    /**
     * Находит все бронирования для конкретной вещи с указанным статусом.
     *
     * @param itemId идентификатор вещи
     * @param status статус бронирования
     * @return список бронирований вещи с указанным статусом
     */
    List<Booking> findAllByItem_IdAndStatus(long itemId, Status status);

    /**
     * Проверяет, существует ли активное подтвержденное бронирование для вещи
     * на указанный период времени.
     *
     * @param itemId идентификатор вещи
     * @param start  начало периода
     * @param end    конец периода
     * @return {@code true}, если существует активное бронирование, пересекающееся с указанным периодом
     */
    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.item.id = :itemId AND b.status = 'APPROVED'
            AND NOT (b.end <= :start OR b.start >= :end)
            """)
    boolean existsActiveBookingForItem(
            @Param("itemId") Long itemId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Проверяет, существуют ли бронирования у пользователя.
     *
     * @param bookerId идентификатор пользователя-арендатора
     * @return {@code true}, если у пользователя есть хотя бы одно бронирование
     */
    boolean existsByBooker_Id(long bookerId);

    /**
     * Проверяет, существуют ли бронирования для вещей владельца.
     *
     * @param ownerId идентификатор владельца вещей
     * @return {@code true}, если для вещей владельца есть хотя бы одно бронирование
     */
    boolean existsByItem_Owner_Id(long ownerId);

    /**
     * Находит бронирование по ID с полной загрузкой связанных сущностей.
     * <p>
     * Загружает бронирование вместе с данными о пользователе-арендаторе,
     * вещи и владельце вещи. Использует JOIN FETCH для избежания N+1 проблемы.
     * </p>
     *
     * @param id идентификатор бронирования
     * @return {@link Optional}, содержащий бронирование со связанными сущностями, или пустой {@link Optional}
     */
    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.booker " +
           "LEFT JOIN FETCH b.item " +
           "LEFT JOIN FETCH b.item.owner " +
           "WHERE b.id = :id")
    Optional<Booking> findByIdWithRelations(@Param("id") Long id);

    /**
     * Находит бронирование по ID с загрузкой вещи и её владельца.
     * <p>
     * Загружает бронирование вместе с данными о вещи и владельце вещи.
     * Использует JOIN FETCH для избежания N+1 проблемы.
     * </p>
     *
     * @param id идентификатор бронирования
     * @return {@link Optional}, содержащий бронирование с вещью и владельцем, или пустой {@link Optional}
     */
    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.item " +
           "LEFT JOIN FETCH b.item.owner " +
           "WHERE b.id = :id")
    Optional<Booking> findByIdWithItemAndOwner(@Param("id") Long id);
}