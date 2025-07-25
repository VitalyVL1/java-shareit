package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private Long id;

    @NotBlank(message = "Название должно быть указано")
    private String name;
    private String description;

    @NotNull
    private boolean available;

    @NotNull(message = "Владелец должен быть указан")
    private User owner;

    private ItemRequest request;

    public Item copyOf(){
        return this.toBuilder().build();
    }
}
