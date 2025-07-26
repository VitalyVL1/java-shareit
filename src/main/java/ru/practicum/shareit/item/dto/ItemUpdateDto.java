package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.util.Optional;

@Data
public class ItemUpdateDto {
    private Optional<String> name;
    private Optional<String> description;
    private Optional<Boolean> available;

    public ItemUpdateDto(String name, String description, Boolean available) {
        this.name = Optional.ofNullable(name);
        this.description = Optional.ofNullable(description);
        this.available = Optional.ofNullable(available);
    }
}
