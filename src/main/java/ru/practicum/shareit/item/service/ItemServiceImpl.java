package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.UpdateItemCommand;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemResponseDto save(Long userId, ItemCreateDto dto) {
        User owner = getUserById(userId);
        Item item = ItemMapper.toItem(owner, dto);
        return ItemMapper.toItemResponseDto(itemRepository.save(item));
    }

    @Override
    public ItemResponseDto findById(Long id) {
        return itemRepository.findById(id)
                .map(ItemMapper::toItemResponseDto)
                .orElseThrow(() -> new NotFoundException("Item", id));
    }

    @Override
    public List<ItemResponseDto> findAll() {
        return ItemMapper.toItemResponseDtoList(itemRepository.findAll());
    }

    @Override
    public List<ItemResponseDto> findByUserId(Long userId) {
        getUserById(userId);
        return ItemMapper.toItemResponseDtoList(itemRepository.findAllByOwnerId(userId));
    }

    @Override
    public List<ItemResponseDto> search(String query) {
        if (!StringUtils.hasText(query)) {
            return Collections.emptyList();
        }
        return ItemMapper.toItemResponseDtoList(itemRepository
                .search(query.trim()));
    }

    @Override
    public ItemResponseDto update(UpdateItemCommand command) {
        Item itemToUpdate = itemRepository.findById(command.itemId())
                .orElseThrow(() -> new NotFoundException("Item", command.itemId()));

        if (!Objects.equals(itemToUpdate.getOwner().getId(), command.userId())) {
            throw new NotFoundException("Item not owned by user", command.userId());
        }

        applyUpdates(itemToUpdate, command.updateData());
        return ItemMapper.toItemResponseDto(itemRepository.save(itemToUpdate));
    }

    @Override
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public void clear() {
        itemRepository.deleteAll();
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));
    }

    private void applyUpdates(Item item, ItemUpdateDto dto) {
        Optional.ofNullable(dto.name()).ifPresent(item::setName);
        Optional.ofNullable(dto.description()).ifPresent(item::setDescription);
        Optional.ofNullable(dto.available()).ifPresent(item::setAvailable);
    }
}
