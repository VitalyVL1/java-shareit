package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponseDto save(UserCreateDto dto) {
        return UserMapper.toUserResponseDto(userRepository.save(UserMapper.toUser(dto)));
    }

    @Override
    public UserResponseDto findById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toUserResponseDto)
                .orElseThrow(() -> new NotFoundException("User", id));
    }

    @Override
    public List<UserResponseDto> findAll() {
        return UserMapper.toUserResponseDtoList(userRepository.findAll());
    }

    @Override
    public UserResponseDto update(Long userId, UserUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        applyUpdates(user, dto);

        return UserMapper.toUserResponseDto(userRepository.save(user));
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void clear() {
        userRepository.deleteAll();
    }

    private void applyUpdates(User user, UserUpdateDto dto) {
        Optional.ofNullable(dto.name()).ifPresent(user::setName);
        Optional.ofNullable(dto.email()).ifPresent(user::setEmail);
    }
}
