package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.UpdateItemCommand;

import java.util.List;

@Service
public interface ItemService {
    ItemResponseDto save(Long userId, ItemCreateDto dto);

    ItemResponseDto findById(Long id);

    List<ItemResponseDto> findAll();

    List<ItemResponseDto> findByUserId(Long userId);

    List<ItemResponseDto> search(String query);

    ItemResponseDto update(UpdateItemCommand command);

    void deleteById(Long id);

    void clear();
}
