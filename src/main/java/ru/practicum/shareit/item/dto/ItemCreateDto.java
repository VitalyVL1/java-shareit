package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.springframework.boot.configurationprocessor.metadata.ItemMetadata;
import ru.practicum.shareit.request.model.ItemRequest;

@Data
@Builder
public class ItemCreateDto {
    @NotBlank(message = "Название должно быть указано")
    private String name;
    private String description;
    private boolean available;
    private ItemRequest request; //Если будут передаваться данные о запросе в теле
}
