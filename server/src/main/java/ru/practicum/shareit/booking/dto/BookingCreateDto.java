package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

public record BookingCreateDto(
        @NotNull(message = "Id вещи должно быть указано")
        Long itemId,

        @NotNull(message = "Начало бронирования должно быть указано")
        @FutureOrPresent(message = "Начало бронирования не должно быть раньше текущей даты")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        LocalDateTime start,

        @NotNull(message = "Окончание бронирования должно быть указано")
        @Future(message = "Окончание бронирования должно быть в будущем")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        LocalDateTime end
) {
    @Builder
    public BookingCreateDto {
    }

    @AssertTrue(message = "Дата начала бронирования должна быть раньше даты окончания")
    public boolean isStartBeforeEnd() {
        if (start == null || end == null) {
            return true; // `@NotNull` уже проверяет null, чтобы не дублировать ошибки
        }
        return start.isBefore(end);
    }
}
