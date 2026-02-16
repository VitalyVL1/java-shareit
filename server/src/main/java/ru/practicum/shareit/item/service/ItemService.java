package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

/**
 * Сервис для управления вещами и комментариями в приложении ShareIt.
 * <p>
 * Определяет контракт для выполнения операций с вещами:
 * создание, обновление, получение по различным критериям, поиск, удаление,
 * а также операции с комментариями к вещам.
 * </p>
 *
 * @see ItemResponseDto
 * @see ItemResponseWithCommentsDto
 * @see ItemCreateDto
 * @see UpdateItemCommand
 * @see CommentRequestDto
 * @see CreateCommentCommand
 * @see UpdateCommentCommand
 */
public interface ItemService {

    /**
     * Создает новую вещь.
     *
     * @param userId идентификатор владельца вещи
     * @param dto    DTO с данными для создания вещи
     * @return созданная вещь в виде базового DTO
     */
    ItemResponseDto save(Long userId, ItemCreateDto dto);

    /**
     * Находит вещь по ее идентификатору.
     * <p>
     * Если запрос делает владелец вещи, в ответ добавляется информация
     * о ближайших бронированиях.
     * </p>
     *
     * @param itemId идентификатор вещи
     * @param userId идентификатор пользователя, запрашивающего информацию
     * @return расширенная информация о вещи с комментариями и (для владельца) датами бронирований
     */
    ItemResponseWithCommentsDto findById(Long itemId, Long userId);

    /**
     * Возвращает список всех вещей.
     *
     * @return список всех вещей в виде базовых DTO
     */
    List<ItemResponseDto> findAll();

    /**
     * Возвращает список всех вещей конкретного пользователя.
     * <p>
     * Для каждой вещи добавляется информация о ближайших бронированиях.
     * </p>
     *
     * @param userId идентификатор владельца
     * @return список вещей пользователя с расширенной информацией
     */
    List<ItemResponseWithCommentsDto> findByUserId(Long userId);

    /**
     * Выполняет поиск доступных вещей по тексту в названии или описании.
     *
     * @param query текст для поиска
     * @return список найденных вещей в виде базовых DTO
     */
    List<ItemResponseDto> search(String query);

    /**
     * Обновляет существующую вещь.
     *
     * @param command команда с данными для обновления (идентификаторы пользователя и вещи, обновляемые поля)
     * @return обновленная вещь в виде базового DTO
     */
    ItemResponseDto update(UpdateItemCommand command);

    /**
     * Удаляет вещь по ее идентификатору.
     *
     * @param id идентификатор вещи для удаления
     */
    void deleteById(Long id);

    /**
     * Очищает все вещи из хранилища.
     * <p>
     * Используется в основном для тестирования.
     * </p>
     */
    void clear();

    /**
     * Добавляет комментарий к вещи.
     *
     * @param command команда с данными для создания комментария
     * @return созданный комментарий в виде DTO
     */
    CommentRequestDto addComment(CreateCommentCommand command);

    /**
     * Обновляет существующий комментарий.
     *
     * @param command команда с данными для обновления комментария
     * @return обновленный комментарий в виде DTO
     */
    CommentRequestDto updateComment(UpdateCommentCommand command);

    /**
     * Удаляет комментарий по его идентификатору.
     *
     * @param commentId идентификатор комментария для удаления
     */
    void deleteComment(Long commentId);
}