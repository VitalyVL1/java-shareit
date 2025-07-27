package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public ItemResponseDto save(Long userId, ItemCreateDto dto) {
        log.info("Saving new item: {} to userId: {}", dto, userId);
        User owner = getUserById(userId);
        Item item = ItemMapper.toItem(owner, dto);
        return ItemMapper.toItemResponseDto(itemDao.save(item));
    }

    @Override
    public ItemResponseDto findById(Long id) {
        log.info("Finding item by id: {}", id);
        return itemDao.findById(id)
                .map(ItemMapper::toItemResponseDto)
                .orElseThrow(() -> new NotFoundException("Item", id));
    }

    @Override
    public List<ItemResponseDto> findAll() {
        log.info("Finding all items");
        return itemDao.findAll().stream()
                .map(ItemMapper::toItemResponseDto)
                .toList();
    }

    @Override
    public List<ItemResponseDto> findByUserId(Long userId) {
        log.info("Finding all items by userId: {}", userId);
        return itemDao.findByUser(getUserById(userId)).stream()
                .map(ItemMapper::toItemResponseDto)
                .toList();
    }

    @Override
    public List<ItemResponseDto> search(String query) {
        if (!StringUtils.hasText(query)) {
            return Collections.emptyList();
        }
        return itemDao.search(query.trim()).stream()
                .map(ItemMapper::toItemResponseDto)
                .toList();
    }

    @Override
    public ItemResponseDto update(Long userId, Long itemId, ItemUpdateDto dto) {
        log.info("Updating item: {} with itemId: {} and userId: {}", dto, itemId, userId);
        Item itemToUpdate = itemDao.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item", itemId));

        if (!Objects.equals(itemToUpdate.getOwner().getId(), userId)) {
            throw new NotFoundException("Item not owned by user", itemId);
        }


        dto.getName().ifPresent(itemToUpdate::setName);
        dto.getDescription().ifPresent(itemToUpdate::setDescription);
        dto.getAvailable().ifPresent(itemToUpdate::setAvailable);

        return ItemMapper.toItemResponseDto(itemDao.update(itemToUpdate));
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting item by id: {}", id);
        itemDao.deleteById(id);
    }

    @Override
    public void clear() {
        log.info("Clearing all items");
        itemDao.clear();
    }

    private User getUserById(Long userId) {
        return userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));
    }
}
