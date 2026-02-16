package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для отображения расширенной информации о вещи в модуле server.
 * <p>
 * Используется при возврате данных о вещи владельцу или при просмотре конкретной вещи.
 * Содержит не только основные поля вещи, но и информацию о ближайших бронированиях
 * (для владельца) и список комментариев. Поле {@code comments} исключается из JSON
 * при значении {@code null}, чтобы не перегружать ответ для операций, где комментарии не требуются.
 * </p>
 *
 * @param id           идентификатор вещи
 * @param name         название вещи
 * @param description  описание вещи
 * @param available    флаг доступности вещи для аренды
 * @param requestId    идентификатор запроса, на который отвечает данная вещь (может быть {@code null})
 * @param lastBooking  дата и время последнего завершенного бронирования (только для владельца)
 * @param nextBooking  дата и время следующего подтвержденного бронирования (только для владельца)
 * @param comments     список комментариев к вещи (исключается при сериализации, если {@code null})
 *
 * @see ru.practicum.shareit.item.model.Item
 * @see CommentRequestDto
 * @see ru.practicum.shareit.item.ItemController
 * @see ru.practicum.shareit.item.service.ItemService
 */
public record ItemResponseWithCommentsDto(
        Long id,
        String name,
        String description,
        boolean available,
        Long requestId,
        LocalDateTime lastBooking,
        LocalDateTime nextBooking,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<CommentRequestDto> comments
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать экземпляры DTO с удобной цепочкой вызовов.
     */
    @Builder
    public ItemResponseWithCommentsDto {
    }
}