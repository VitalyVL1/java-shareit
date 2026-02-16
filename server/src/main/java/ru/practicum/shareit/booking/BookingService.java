package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingApproveDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.State;

import java.util.List;

/**
 * Сервис для управления бронированиями в приложении ShareIt.
 * <p>
 * Определяет контракт для выполнения операций с бронированиями:
 * создание, подтверждение, получение по различным критериям,
 * удаление и очистка.
 * </p>
 *
 * @see BookingResponseDto
 * @see BookingCreateDto
 * @see BookingApproveDto
 * @see State
 */
public interface BookingService {

    /**
     * Создает новое бронирование.
     *
     * @param bookerId идентификатор пользователя, создающего бронирование
     * @param dto      DTO с данными для создания бронирования
     * @return созданное бронирование в виде DTO
     */
    BookingResponseDto save(Long bookerId, BookingCreateDto dto);

    /**
     * Находит бронирование по его идентификатору.
     * <p>
     * Доступно только автору бронирования или владельцу вещи.
     * </p>
     *
     * @param bookingId идентификатор бронирования
     * @param userId    идентификатор пользователя, запрашивающего информацию
     * @return найденное бронирование в виде DTO
     */
    BookingResponseDto findById(Long bookingId, Long userId);

    /**
     * Находит все бронирования пользователя (арендатора) с фильтрацией по статусу.
     *
     * @param bookerId идентификатор пользователя-арендатора
     * @param state    статус для фильтрации
     * @return список бронирований пользователя
     */
    List<BookingResponseDto> findByBookerIdAndState(Long bookerId, State state);

    /**
     * Находит все бронирования для вещей владельца с фильтрацией по статусу.
     *
     * @param ownerId идентификатор владельца вещей
     * @param state   статус для фильтрации
     * @return список бронирований для вещей владельца
     */
    List<BookingResponseDto> findByOwnerIdAndState(Long ownerId, State state);

    /**
     * Подтверждает или отклоняет бронирование.
     * <p>
     * Доступно только владельцу вещи.
     * </p>
     *
     * @param dto DTO с идентификатором бронирования, идентификатором владельца и флагом подтверждения
     * @return обновленное бронирование в виде DTO
     */
    BookingResponseDto approve(BookingApproveDto dto);

    /**
     * Удаляет бронирование по его идентификатору.
     *
     * @param id идентификатор бронирования для удаления
     */
    void deleteById(Long id);

    /**
     * Очищает все бронирования из хранилища.
     * <p>
     * Используется в основном для тестирования.
     * </p>
     */
    void clear();
}