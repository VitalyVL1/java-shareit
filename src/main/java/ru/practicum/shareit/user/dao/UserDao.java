package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User save(User user);

    Optional<User> findById(Long id);

    List<User> findAll();

    User update(User user);

    void deleteById(Long id);

    void clear();
}
