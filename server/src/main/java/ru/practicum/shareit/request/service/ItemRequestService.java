package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponseDto save(Long requestorId, ItemRequestCreateDto dto);

    ItemRequestResponseDto findById(Long itemRequestId);

    List<ItemRequestResponseDto> findAll();

    List<ItemRequestResponseDto> findByUserId(Long requestorId);

    void deleteById(Long itemRequestId);

    void clear();
}
