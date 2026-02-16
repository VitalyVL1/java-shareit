package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Утилитарный класс для преобразования между сущностью {@link Item} и соответствующими DTO.
 * <p>
 * Содержит статические методы для преобразования объектов вещей в различные
 * форматы DTO и обратно. Используется в сервисном слое для изоляции логики
 * преобразования и предотвращения циклических зависимостей.
 * </p>
 *
 * @see Item
 * @see ItemResponseDto
 * @see ItemResponseWithCommentsDto
 * @see ItemShortDto
 * @see ItemForRequestDto
 * @see ItemCreateDto
 */
public class ItemMapper {

    /**
     * Преобразует сущность {@link Item} в {@link ItemResponseDto}.
     * <p>
     * Используется для базового представления вещи без дополнительной информации
     * о бронированиях и комментариях.
     * </p>
     *
     * @param item сущность вещи (может быть {@code null})
     * @return DTO с основной информацией о вещи или {@code null}, если входной параметр равен {@code null}
     */
    public static ItemResponseDto toItemResponseDto(Item item) {
        if (item == null) return null;

        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(
                        item.getRequest() != null ?
                                item.getRequest().getId() : null)
                .build();
    }

    /**
     * Преобразует список сущностей {@link Item} в список {@link ItemResponseDto}.
     * <p>
     * Применяет {@link #toItemResponseDto(Item)} к каждому элементу списка.
     * </p>
     *
     * @param items список сущностей вещей (может быть {@code null} или пустым)
     * @return список DTO с основной информацией о вещах или пустой список,
     *         если входной параметр равен {@code null} или пуст
     */
    public static List<ItemResponseDto> toItemResponseDto(List<Item> items) {
        if (items == null || items.isEmpty()) return Collections.emptyList();
        return items.stream()
                .map(ItemMapper::toItemResponseDto)
                .toList();
    }

    /**
     * Преобразует сущность {@link Item} в {@link ItemShortDto}.
     * <p>
     * Используется для минимального представления вещи (только ID и название)
     * при встраивании в другие DTO.
     * </p>
     *
     * @param item сущность вещи (может быть {@code null})
     * @return DTO с ID и названием вещи или {@code null}, если входной параметр равен {@code null}
     */
    public static ItemShortDto toItemShortDto(Item item) {
        if (item == null) return null;

        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    /**
     * Преобразует сущность {@link Item} в {@link ItemResponseWithCommentsDto}.
     * <p>
     * Используется для расширенного представления вещи с информацией о ближайших
     * бронированиях и комментариях. Применяется при просмотре конкретной вещи
     * или при выводе списка вещей владельца.
     * </p>
     *
     * @param item        сущность вещи
     * @param lastBooking дата последнего завершенного бронирования (может быть {@code null})
     * @param nextBooking дата следующего подтвержденного бронирования (может быть {@code null})
     * @param comments    список комментариев к вещи (может быть пустым)
     * @return DTO с расширенной информацией о вещи или {@code null}, если вещь равна {@code null}
     */
    public static ItemResponseWithCommentsDto toItemResponseWithCommentsDto(
            Item item,
            LocalDateTime lastBooking,
            LocalDateTime nextBooking,
            List<CommentRequestDto> comments) {
        if (item == null) return null;

        return ItemResponseWithCommentsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(
                        item.getRequest() != null ?
                                item.getRequest().getId() : null)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

    /**
     * Создает сущность {@link Item} из DTO создания и связанных сущностей.
     * <p>
     * Используется при добавлении новой вещи.
     * </p>
     *
     * @param owner   владелец вещи
     * @param request запрос, на который отвечает вещь (может быть {@code null})
     * @param dto     DTO с данными для создания вещи
     * @return новая сущность вещи или {@code null}, если DTO равен {@code null}
     */
    public static Item toItem(User owner, ItemRequest request, ItemCreateDto dto) {
        if (dto == null) return null;

        return Item.builder()
                .name(dto.name())
                .description(dto.description())
                .available(dto.available())
                .owner(owner)
                .request(request)
                .build();
    }

    /**
     * Преобразует сущность {@link Item} в {@link ItemForRequestDto}.
     * <p>
     * Используется при формировании ответа на запрос вещи для отображения
     * предложенных вещей.
     * </p>
     *
     * @param item сущность вещи (может быть {@code null})
     * @return DTO для отображения в контексте запроса или {@code null}, если входной параметр равен {@code null}
     */
    public static ItemForRequestDto toItemForRequestDto(Item item) {
        if (item == null) return null;
        return ItemForRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }

    /**
     * Преобразует список сущностей {@link Item} в список {@link ItemForRequestDto}.
     * <p>
     * Применяет {@link #toItemForRequestDto(Item)} к каждому элементу списка.
     * </p>
     *
     * @param items список сущностей вещей (может быть {@code null} или пустым)
     * @return список DTO для отображения в контексте запросов или пустой список,
     *         если входной параметр равен {@code null} или пуст
     */
    public static List<ItemForRequestDto> toItemForRequestDto(List<Item> items) {
        if (items == null || items.isEmpty()) return Collections.emptyList();
        return items.stream()
                .map(ItemMapper::toItemForRequestDto)
                .toList();
    }
}