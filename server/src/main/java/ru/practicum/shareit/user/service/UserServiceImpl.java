package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserResponseDto save(UserCreateDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new DuplicatedDataException("email", dto.email());
        }
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
        return UserMapper.toUserResponseDto(userRepository.findAll());
    }

    @Transactional
    @Override
    public UserResponseDto update(Long userId, UserUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        if (dto.email() != null &&
            !dto.email().equals(user.getEmail()) &&
            userRepository.existsByEmail(dto.email())) {
            throw new DuplicatedDataException("email", dto.email());
        }

        applyUpdates(user, dto);

        return UserMapper.toUserResponseDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void clear() {
        userRepository.deleteAll();
    }

    private void applyUpdates(User user, UserUpdateDto dto) {
        Optional.ofNullable(dto.name()).ifPresent(user::setName);
        Optional.ofNullable(dto.email()).ifPresent(user::setEmail);
    }
}
