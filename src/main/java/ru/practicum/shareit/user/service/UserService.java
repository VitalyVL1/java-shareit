package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMaper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;

    public UserResponseDto save(UserCreateDto dto) {
        log.info("Saving user: {}", dto);

        return UserMaper.toUserResponseDto(userDao.save(UserMaper.toUser(dto)));
    }


    public UserResponseDto findById(Long id) {
        log.info("Finding user by id: {}", id);
        return userDao.findById(id)
                .map(UserMaper::toUserResponseDto)
                .orElseThrow(() -> new NotFoundException("User", id));
    }

    public List<UserResponseDto> findAll() {
        log.info("Finding all users");
        return userDao.findAll().stream()
                .map(UserMaper::toUserResponseDto)
                .toList();
    }

    public UserResponseDto update(Long userId, UserUpdateDto dto) {
        log.info("Updating user with id = {}", userId);

        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        dto.getName().ifPresent(user::setName);
        dto.getEmail().ifPresent(user::setEmail);

        return UserMaper.toUserResponseDto(userDao.update(user));
    }

    public void deleteById(Long id) {
        log.info("Deleting user with id = {}", id);
        userDao.deleteById(id);
    }

    ;

    public void clear() {
        log.info("Clearing all users");
        userDao.clear();
    }

    ;
}
