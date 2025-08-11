package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
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
    private final UserDao userDao;

    @Override
    public UserResponseDto save(UserCreateDto dto) {
        return UserMapper.toUserResponseDto(userDao.save(UserMapper.toUser(dto)));
    }

    @Override
    public UserResponseDto findById(Long id) {
        return userDao.findById(id)
                .map(UserMapper::toUserResponseDto)
                .orElseThrow(() -> new NotFoundException("User", id));
    }

    @Override
    public List<UserResponseDto> findAll() {
        return UserMapper.toUserResponseDtoList(userDao.findAll());
    }

    @Override
    public UserResponseDto update(Long userId, UserUpdateDto dto) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        applyUpdates(user, dto);

        return UserMapper.toUserResponseDto(userDao.update(user));
    }

    @Override
    public void deleteById(Long id) {
        userDao.deleteById(id);
    }

    @Override
    public void clear() {
        userDao.clear();
    }

    private void applyUpdates(User user, UserUpdateDto dto) {
        Optional.ofNullable(dto.name()).ifPresent(user::setName);
        Optional.ofNullable(dto.email()).ifPresent(user::setEmail);
    }
}
