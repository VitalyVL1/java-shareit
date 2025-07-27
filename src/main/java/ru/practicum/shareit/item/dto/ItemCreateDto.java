package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

@Data
@Builder
public class ItemCreateDto {
    @NotBlank(message = "Название должно быть указано")
    private String name;

    @NotBlank(message = "Описание должно быть указано")
    private String description;

    @NotNull(message = "Доступность вещи должна быть задана")
    private Boolean available;

    private ItemRequest request; //Если будут передаваться данные о запросе в теле
}
