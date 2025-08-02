package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    UserResponseDto save(UserCreateDto dto);

    UserResponseDto findById(Long id);

    List<UserResponseDto> findAll();

    UserResponseDto update(Long userId, UserUpdateDto dto);

    void deleteById(Long id);

    void clear();
}