package ru.practicum.shareit.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.Optional;

@Data
public class UserUpdateDto {
    private Optional<String> name;

    @Valid
    private Optional<@Email String> email;

    public UserUpdateDto(String name, String email) {
        this.name = Optional.ofNullable(name);
        this.email = Optional.ofNullable(email);
    }
}
