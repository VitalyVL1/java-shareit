package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;

    @NotBlank(message = "Имя должно быть указано")
    private String name;

    @Email(message = "Некорректный email")
    @NotBlank(message = "Email должен быть указан")
    private String email;

    public User copyOf() {
        return this.toBuilder().build();
    }
}
