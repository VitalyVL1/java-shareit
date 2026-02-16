package ru.practicum.shareit.request;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Утилитарный класс для преобразования между сущностью {@link ItemRequest} и соответствующими DTO.
 * <p>
 * Содержит статические методы для преобразования объектов запросов в различные
 * форматы DTO и обратно. Используется в сервисном слое для изоляции логики
 * преобразования и предотвращения циклических зависимостей.
 * </p>
 *
 * @see ItemRequest
 * @see ItemRequestResponseDto
 * @see ItemRequestCreateDto
 */
public class ItemRequestMapper {

    /**
     * Преобразует сущность {@link ItemRequest} и список связанных вещей в {@link ItemRequestResponseDto}.
     * <p>
     * Использует {@link ItemMapper#toItemForRequestDto(List)} для преобразования списка вещей.
     * </p>
     *
     * @param itemRequest сущность запроса (может быть {@code null})
     * @param items       список вещей, созданных в ответ на данный запрос
     * @return DTO с полной информацией о запросе или {@code null}, если входной параметр равен {@code null}
     */
    public static ItemRequestResponseDto toItemRequestResponseDto(ItemRequest itemRequest, List<Item> items) {
        if (itemRequest == null) return null;

        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorId(itemRequest.getRequestor().getId())
                .items(new HashSet<>(ItemMapper.toItemForRequestDto(items)))
                .created(itemRequest.getCreated())
                .build();
    }

    /**
     * Преобразует список сущностей {@link ItemRequest} в список {@link ItemRequestResponseDto}
     * с использованием карты вещей, сгруппированных по идентификаторам запросов.
     * <p>
     * Позволяет эффективно преобразовывать множество запросов, заранее загрузив все связанные вещи
     * и сгруппировав их по запросам (для избежания N+1 проблемы).
     * </p>
     *
     * @param itemRequests       список сущностей запросов (может быть {@code null})
     * @param itemsByRequestId   карта, где ключ - идентификатор запроса, значение - список вещей для этого запроса
     * @return список DTO с информацией о запросах или пустой список, если входной параметр равен {@code null}
     */
    public static List<ItemRequestResponseDto> toItemRequestResponseDto(
            List<ItemRequest> itemRequests,
            Map<Long, List<Item>> itemsByRequestId
    ) {
        if (itemRequests == null) return Collections.emptyList();

        return itemRequests.stream()
                .map(itemRequest -> toItemRequestResponseDto(
                        itemRequest,
                        itemsByRequestId.getOrDefault(itemRequest.getId(), Collections.emptyList())
                ))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Создает сущность {@link ItemRequest} из DTO создания и связанной сущности пользователя.
     * <p>
     * Используется при создании нового запроса. Дата создания запроса не устанавливается,
     * так как она будет автоматически заполнена в сущности (через {@code @Builder.Default}).
     * </p>
     *
     * @param requestor пользователь, создающий запрос
     * @param dto       DTO с описанием желаемой вещи
     * @return новая сущность запроса или {@code null}, если DTO равен {@code null}
     */
    public static ItemRequest toItemRequest(User requestor, ItemRequestCreateDto dto) {
        if (dto == null) return null;

        return ItemRequest.builder()
                .requestor(requestor)
                .description(dto.description())
                .build();
    }
}