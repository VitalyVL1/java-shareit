package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemDao {
    Item save(Item item);

    Optional<Item> findById(Long id);

    List<Item> findAll();

    Item update(Item item);

    void deleteById(Long id);

    void clear();
}
