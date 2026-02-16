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

/**
 * Реализация сервиса {@link ItemRequestService} для управления запросами вещей.
 * <p>
 * Обеспечивает бизнес-логику для работы с запросами вещей: создание, получение
 * по различным критериям, удаление. При формировании ответов добавляет информацию
 * о вещах, созданных в ответ на каждый запрос.
 * </p>
 *
 * @see ItemRequestService
 * @see ItemRequestRepository
 * @see UserRepository
 * @see ItemRepository
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    /**
     * Создает новый запрос вещи.
     * <p>
     * Сохраняет запрос в базе данных. На данном этапе список предложенных вещей пуст.
     * </p>
     *
     * @param requestorId идентификатор пользователя, создающего запрос
     * @param dto         DTO с описанием желаемой вещи
     * @return созданный запрос в виде DTO
     * @throws NotFoundException если пользователь не найден
     */
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

    /**
     * Находит запрос по его идентификатору.
     * <p>
     * Возвращает запрос вместе со списком вещей, которые были предложены
     * в ответ на данный запрос.
     * </p>
     *
     * @param itemRequestId идентификатор запроса
     * @return найденный запрос в виде DTO
     * @throws NotFoundException если запрос не найден
     */
    @Override
    public ItemRequestResponseDto findById(Long itemRequestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(
                () -> new NotFoundException("ItemRequest", itemRequestId)
        );
        List<Item> items = itemRepository.findAllByRequestId(itemRequestId);
        return ItemRequestMapper.toItemRequestResponseDto(itemRequest, items);
    }

    /**
     * Возвращает список всех запросов, созданных другими пользователями.
     * <p>
     * Запросы сортируются по дате создания в порядке убывания.
     * Для каждого запроса добавляется список предложенных вещей.
     * </p>
     *
     * @return список всех запросов с предложенными вещами
     */
    @Override
    public List<ItemRequestResponseDto> findAll() {
        Map<Long, List<Item>> itemsWithRequest = itemRepository.findAllByRequestNotNull().stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
        List<ItemRequest> itemRequests = itemRequestRepository.findAll(Sort.by("created").descending());
        return ItemRequestMapper.toItemRequestResponseDto(itemRequests, itemsWithRequest);
    }

    /**
     * Возвращает список всех запросов, созданных конкретным пользователем.
     * <p>
     * Запросы сортируются по дате создания в порядке убывания.
     * Для каждого запроса добавляется список предложенных вещей.
     * </p>
     *
     * @param requestorId идентификатор пользователя
     * @return список запросов пользователя с предложенными вещами
     * @throws NotFoundException если пользователь не найден
     */
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

    /**
     * Удаляет запрос по его идентификатору.
     *
     * @param itemRequestId идентификатор запроса для удаления
     */
    @Transactional
    @Override
    public void deleteById(Long itemRequestId) {
        itemRequestRepository.deleteById(itemRequestId);
    }

    /**
     * Очищает все запросы из хранилища.
     * <p>
     * Используется в основном для тестирования.
     * </p>
     */
    @Transactional
    @Override
    public void clear() {
        itemRequestRepository.deleteAll();
    }

    /**
     * Получает пользователя по идентификатору или выбрасывает исключение.
     *
     * @param userId идентификатор пользователя
     * @return найденный пользователь
     * @throws NotFoundException если пользователь не найден
     */
    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));
    }
}