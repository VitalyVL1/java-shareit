package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestResponseDto save(Long requestorId, ItemRequestCreateDto dto) {
        User requestor = getUserOrThrow(requestorId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestor, dto);
        return ItemRequestMapper.toItemRequestResponseDto(
                itemRequestRepository.save(itemRequest),
                Collections.emptyList()
        );
    }

    @Override
    public ItemRequestResponseDto findById(Long itemRequestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(
                () -> new NotFoundException("ItemRequest", itemRequestId)
        );
        List<Item> items = itemRepository.findAllByRequestId(itemRequestId);
        return ItemRequestMapper.toItemRequestResponseDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestResponseDto> findAll() {
        Map<Long, List<Item>> itemsWithRequest = itemRepository.findAllByRequestNotNull().stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
        List<ItemRequest> itemRequests = itemRequestRepository.findAll(Sort.by("created").descending());
        return ItemRequestMapper.toItemRequestResponseDto(itemRequests, itemsWithRequest);
    }

    @Override
    public List<ItemRequestResponseDto> findByUserId(Long requestorId) {
        getUserOrThrow(requestorId);

        Map<Long, List<Item>> itemsByRequestIds = itemRepository.findAllByRequest_Requestor_Id(requestorId).stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_Id(
                requestorId,
                Sort.by("created").descending());
        return ItemRequestMapper.toItemRequestResponseDto(itemRequests, itemsByRequestIds);
    }

    @Transactional
    @Override
    public void deleteById(Long itemRequestId) {
        itemRequestRepository.deleteById(itemRequestId);
    }

    @Transactional
    @Override
    public void clear() {
        itemRequestRepository.deleteAll();
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));
    }
}
