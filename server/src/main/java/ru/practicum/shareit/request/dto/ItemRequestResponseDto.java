package ru.practicum.shareit.request.dto;

import lombok.Builder;
import ru.practicum.shareit.item.dto.ItemForRequestDto;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO для отображения информации о запросе вещи в модуле server.
 * <p>
 * Используется при возврате данных о запросе клиенту. Содержит полную информацию
 * о запросе: идентификатор, описание, идентификатор автора запроса,
 * дату создания и список вещей, которые были предложены в ответ на данный запрос.
 * </p>
 *
 * @param id          идентификатор запроса
 * @param description описание желаемой вещи
 * @param requestorId идентификатор пользователя, создавшего запрос
 * @param items       множество вещей, созданных в ответ на данный запрос
 * @param created     дата и время создания запроса
 *
 * @see ru.practicum.shareit.request.ItemRequest
 * @see ru.practicum.shareit.item.dto.ItemForRequestDto
 * @see ru.practicum.shareit.request.ItemRequestController
 * @see ru.practicum.shareit.request.service.ItemRequestService
 */
@Builder(toBuilder = true)
public record ItemRequestResponseDto(
        Long id,
        String description,
        Long requestorId,
        Set<ItemForRequestDto> items,
        LocalDateTime created
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать экземпляры DTO с удобной цепочкой вызовов.
     */
    @Builder
    public ItemRequestResponseDto {
    }
}