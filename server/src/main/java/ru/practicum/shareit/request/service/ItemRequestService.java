package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

/**
 * Сервис для управления запросами вещей в приложении ShareIt.
 * <p>
 * Определяет контракт для выполнения операций с запросами вещей:
 * создание, получение по различным критериям, удаление и очистка.
 * </p>
 *
 * @see ItemRequestResponseDto
 * @see ItemRequestCreateDto
 * @see ru.practicum.shareit.request.ItemRequest
 */
public interface ItemRequestService {

    /**
     * Создает новый запрос вещи.
     *
     * @param requestorId идентификатор пользователя, создающего запрос
     * @param dto         DTO с описанием желаемой вещи
     * @return созданный запрос в виде DTO
     */
    ItemRequestResponseDto save(Long requestorId, ItemRequestCreateDto dto);

    /**
     * Находит запрос по его идентификатору.
     *
     * @param itemRequestId идентификатор запроса
     * @return найденный запрос в виде DTO
     */
    ItemRequestResponseDto findById(Long itemRequestId);

    /**
     * Возвращает список всех запросов, созданных другими пользователями.
     * <p>
     * Используется для просмотра доступных запросов, на которые можно предложить свои вещи.
     * </p>
     *
     * @return список всех запросов
     */
    List<ItemRequestResponseDto> findAll();

    /**
     * Возвращает список всех запросов, созданных конкретным пользователем.
     *
     * @param requestorId идентификатор пользователя
     * @return список запросов пользователя
     */
    List<ItemRequestResponseDto> findByUserId(Long requestorId);

    /**
     * Удаляет запрос по его идентификатору.
     *
     * @param itemRequestId идентификатор запроса для удаления
     */
    void deleteById(Long itemRequestId);

    /**
     * Очищает все запросы из хранилища.
     * <p>
     * Используется в основном для тестирования.
     * </p>
     */
    void clear();
}