package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO для создания нового бронирования в модуле gateway.
 * <p>
 * Содержит данные, необходимые для создания запроса на бронирование вещи:
 * идентификатор вещи, дату и время начала и окончания бронирования.
 * Включает аннотации валидации для проверки корректности входящих данных
 * на уровне шлюза перед отправкой запроса на сервер.
 * </p>
 */
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
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать экземпляры DTO с удобной цепочкой вызовов.
     */
    @Builder
    public BookingCreateDto {
    }

    /**
     * Проверяет, что дата начала бронирования раньше даты окончания.
     * <p>
     * Данный метод используется фреймворком валидации Jakarta (Bean Validation)
     * благодаря аннотации {@link AssertTrue}. Если {@code start} или {@code end} равны {@code null},
     * метод возвращает {@code true}, чтобы не дублировать сообщения об ошибках
     * от аннотаций {@link NotNull}.
     * </p>
     *
     * @return {@code true}, если {@code start} раньше {@code end}, или если любое из полей равно {@code null};
     *         {@code false}, если {@code start} не раньше {@code end}
     */
    @AssertTrue(message = "Дата начала бронирования должна быть раньше даты окончания")
    public boolean isStartBeforeEnd() {
        if (start == null || end == null) {
            return true;
        }
        return start.isBefore(end);
    }
}