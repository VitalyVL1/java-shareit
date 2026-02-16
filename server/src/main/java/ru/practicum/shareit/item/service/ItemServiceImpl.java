package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.AccessForbiddenException;
import ru.practicum.shareit.exception.CommentNotAllowedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Реализация сервиса {@link ItemService} для управления вещами и комментариями.
 * <p>
 * Обеспечивает бизнес-логику для работы с вещами: создание, обновление,
 * получение по различным критериям, поиск, удаление, а также управление комментариями.
 * Включает проверки прав доступа и валидацию бизнес-правил.
 * </p>
 *
 * @see ItemService
 * @see ItemRepository
 * @see UserRepository
 * @see CommentRepository
 * @see BookingRepository
 * @see ItemRequestRepository
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    /**
     * Создает новую вещь.
     * <p>
     * Если указан {@code requestId}, проверяет существование соответствующего запроса
     * и связывает вещь с ним.
     * </p>
     *
     * @param userId идентификатор владельца вещи
     * @param dto    DTO с данными для создания вещи
     * @return созданная вещь в виде базового DTO
     * @throws NotFoundException если пользователь не найден или запрос с указанным ID не существует
     */
    @Transactional
    @Override
    public ItemResponseDto save(Long userId, ItemCreateDto dto) {
        User owner = getUserById(userId);
        ItemRequest itemRequest = Optional.ofNullable(dto.requestId())
                .flatMap(itemRequestRepository::findById)
                .orElse(null);
        Item item = ItemMapper.toItem(owner, itemRequest, dto);
        return ItemMapper.toItemResponseDto(itemRepository.save(item));
    }

    /**
     * Находит вещь по ее идентификатору.
     * <p>
     * Если запрос делает владелец вещи, вычисляет даты последнего и следующего
     * подтвержденных бронирований. Всегда добавляет список комментариев к вещи.
     * </p>
     *
     * @param itemId идентификатор вещи
     * @param userId идентификатор пользователя, запрашивающего информацию
     * @return расширенная информация о вещи с комментариями и (для владельца) датами бронирований
     * @throws NotFoundException если вещь не найдена
     */
    @Override
    public ItemResponseWithCommentsDto findById(Long itemId, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        Item item = getItemById(itemId);
        List<Booking> bookings;
        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);

        LocalDateTime lastBooking = null;
        LocalDateTime nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            bookings = bookingRepository.findAllByItem_IdAndStatus(itemId, Status.APPROVED);

            lastBooking = bookings.stream()
                    .map(Booking::getEnd)
                    .filter(end -> end.isBefore(now))
                    .max(Comparator.naturalOrder())
                    .orElse(null);


            nextBooking = bookings.stream()
                    .map(Booking::getStart)
                    .filter(start -> start.isAfter(now))
                    .min(Comparator.naturalOrder())
                    .orElse(null);
        }

        return ItemMapper.toItemResponseWithCommentsDto(
                item,
                lastBooking,
                nextBooking,
                CommentMapper.toCommentRequestDto(comments)
        );
    }

    /**
     * Возвращает список всех вещей.
     *
     * @return список всех вещей в виде базовых DTO
     */
    @Override
    public List<ItemResponseDto> findAll() {
        return ItemMapper.toItemResponseDto(itemRepository.findAll());
    }

    /**
     * Возвращает список всех вещей конкретного пользователя.
     * <p>
     * Для каждой вещи вычисляет даты последнего и следующего подтвержденных бронирований.
     * </p>
     *
     * @param userId идентификатор владельца
     * @return список вещей пользователя с расширенной информацией (без комментариев)
     * @throws NotFoundException если пользователь не найден
     */
    @Override
    public List<ItemResponseWithCommentsDto> findByUserId(Long userId) {
        getUserById(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<Booking> bookings = bookingRepository.findAllByItem_Owner_IdAndStatus(
                userId,
                Status.APPROVED);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastBooking;
        LocalDateTime nextBooking;

        List<ItemResponseWithCommentsDto> itemResponseDtos = new ArrayList<>();

        for (Item item : items) {
            lastBooking = bookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()))
                    .map(Booking::getEnd)
                    .filter(end -> end.isBefore(now))
                    .max(Comparator.naturalOrder())
                    .orElse(null);

            nextBooking = bookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()))
                    .map(Booking::getStart)
                    .filter(start -> start.isAfter(now))
                    .min(Comparator.naturalOrder())
                    .orElse(null);
            itemResponseDtos.add(ItemMapper
                    .toItemResponseWithCommentsDto(item, lastBooking, nextBooking, null));
        }

        return itemResponseDtos;
    }

    /**
     * Выполняет поиск доступных вещей по тексту в названии или описании.
     * <p>
     * Поиск регистронезависимый, возвращаются только вещи с флагом available = true.
     * Если текст запроса пустой или состоит только из пробелов, возвращается пустой список.
     * </p>
     *
     * @param query текст для поиска
     * @return список найденных вещей в виде базовых DTO
     */
    @Override
    public List<ItemResponseDto> search(String query) {
        if (!StringUtils.hasText(query)) {
            return Collections.emptyList();
        }
        return ItemMapper.toItemResponseDto(itemRepository
                .search(query.trim()));
    }

    /**
     * Обновляет существующую вещь.
     * <p>
     * Проверяет, что пользователь является владельцем вещи.
     * Обновляет только те поля, которые были переданы (не {@code null}).
     * </p>
     *
     * @param command команда с данными для обновления
     * @return обновленная вещь в виде базового DTO
     * @throws NotFoundException если вещь не найдена или пользователь не является владельцем
     */
    @Transactional
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

    /**
     * Удаляет вещь по ее идентификатору.
     *
     * @param id идентификатор вещи для удаления
     */
    @Transactional
    @Override
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    /**
     * Очищает все вещи из хранилища.
     * <p>
     * Используется в основном для тестирования.
     * </p>
     */
    @Transactional
    @Override
    public void clear() {
        itemRepository.deleteAll();
    }

    /**
     * Добавляет комментарий к вещи.
     * <p>
     * Проверяет, что пользователь действительно арендовал эту вещь
     * и что срок аренды уже завершен.
     * </p>
     *
     * @param command команда с данными для создания комментария
     * @return созданный комментарий в виде DTO
     * @throws NotFoundException если пользователь, вещь или бронирование не найдены
     * @throws CommentNotAllowedException если комментарий оставляется до окончания бронирования
     */
    @Transactional
    @Override
    public CommentRequestDto addComment(CreateCommentCommand command) {
        Sort sort = Sort.by(Sort.Direction.ASC, "end");
        Booking booking = bookingRepository
                .findAllByBooker_IdAndItem_Id(command.authorId(), command.itemId(), sort)
                .stream().findFirst().orElseThrow(
                        () -> new NotFoundException("Booking not found", command.authorId()));

        if (booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new CommentNotAllowedException("Comment before booking's end", command.authorId(), command.itemId());
        }

        User author = getUserById(command.authorId());
        Item item = getItemById(command.itemId());
        Comment comment = CommentMapper.toComment(author, item, command.dto());

        return CommentMapper.toCommentRequestDto(commentRepository.save(comment));
    }

    /**
     * Обновляет существующий комментарий.
     * <p>
     * Проверяет, что пользователь является автором комментария и что комментарий
     * действительно относится к указанной вещи.
     * </p>
     *
     * @param command команда с данными для обновления комментария
     * @return обновленный комментарий в виде DTO
     * @throws NotFoundException если комментарий, пользователь или вещь не найдены
     * @throws AccessForbiddenException если пользователь не является автором комментария
     */
    @Transactional
    @Override
    public CommentRequestDto updateComment(UpdateCommentCommand command) {
        Comment comment = commentRepository.findById(command.commentId())
                .orElseThrow(() -> new NotFoundException("Comment", command.commentId()));
        getUserById(command.authorId()); // для проверки существования такого пользователя
        getItemById(command.itemId()); // для проверки существования вещи

        if (!Objects.equals(comment.getAuthor().getId(), command.authorId())) {
            throw new AccessForbiddenException("Forbidden to change comment not owned by user", command.authorId());
        }

        if (!Objects.equals(comment.getItem().getId(), command.itemId())) {
            throw new NotFoundException("Comment not found to itemId", command.itemId());
        }

        Optional.ofNullable(command.dto().text()).ifPresent(comment::setText);

        return CommentMapper.toCommentRequestDto(commentRepository.save(comment));
    }

    /**
     * Удаляет комментарий по его идентификатору.
     *
     * @param commentId идентификатор комментария для удаления
     */
    @Transactional
    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    /**
     * Применяет обновления к сущности вещи.
     * <p>
     * Обновляет только те поля, которые были переданы в DTO (не {@code null}).
     * </p>
     *
     * @param item вещь для обновления
     * @param dto  DTO с обновляемыми полями
     */
    private void applyUpdates(Item item, ItemUpdateDto dto) {
        Optional.ofNullable(dto.name()).ifPresent(item::setName);
        Optional.ofNullable(dto.description()).ifPresent(item::setDescription);
        Optional.ofNullable(dto.available()).ifPresent(item::setAvailable);
    }

    /**
     * Получает пользователя по идентификатору или выбрасывает исключение.
     *
     * @param userId идентификатор пользователя
     * @return найденный пользователь
     * @throws NotFoundException если пользователь не найден
     */
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));
    }

    /**
     * Получает вещь по идентификатору или выбрасывает исключение.
     *
     * @param itemId идентификатор вещи
     * @return найденная вещь
     * @throws NotFoundException если вещь не найдена
     */
    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item", itemId));
    }
}