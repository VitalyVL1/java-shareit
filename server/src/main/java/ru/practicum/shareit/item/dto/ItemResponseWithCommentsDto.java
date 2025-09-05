package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public record ItemResponseWithCommentsDto(
        Long id,
        String name,
        String description,
        boolean available,
        Long requestId,
        LocalDateTime lastBooking,
        LocalDateTime nextBooking,

        @JsonInclude(JsonInclude.Include.NON_NULL) //исключаем это поле когда оно Null, что бы не перегружать вывод всех Item для владельца комментариями и при этом не создавать отдельный DTO
        List<CommentRequestDto> comments
) {
    @Builder
    public ItemResponseWithCommentsDto {
    }
}
