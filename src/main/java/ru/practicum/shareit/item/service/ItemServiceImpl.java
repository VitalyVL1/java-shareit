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
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public ItemResponseDto save(Long userId, ItemCreateDto dto) {
        User owner = getUserById(userId);
        Item item = ItemMapper.toItem(owner, dto);
        return ItemMapper.toItemResponseDto(itemRepository.save(item));
    }

    @Override
    public ItemResponseWithCommentsDto findById(Long itemId, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        Item item = getItemById(itemId);
        List<Booking> bookings = bookingRepository.findAllByItem_IdAndStatus(itemId, Status.APPROVED);
        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);

        LocalDateTime lastBooking = null;
        LocalDateTime nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
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

    @Override
    public List<ItemResponseDto> findAll() {
        return ItemMapper.toItemResponseDto(itemRepository.findAll());
    }

    @Override
    public List<ItemResponseDto> findByUserId(Long userId) {
        getUserById(userId);
        return ItemMapper.toItemResponseDto(itemRepository.findAllByOwnerId(userId));
    }

    @Override
    public List<ItemResponseDto> search(String query) {
        if (!StringUtils.hasText(query)) {
            return Collections.emptyList();
        }
        return ItemMapper.toItemResponseDto(itemRepository
                .search(query.trim()));
    }

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

    @Transactional
    @Override
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void clear() {
        itemRepository.deleteAll();
    }

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

        Optional.ofNullable(command.dto().text()).ifPresent(comment::setText); //т.к. меняется одно поле то в отдельный метод выделять не стал

        return CommentMapper.toCommentRequestDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private void applyUpdates(Item item, ItemUpdateDto dto) {
        Optional.ofNullable(dto.name()).ifPresent(item::setName);
        Optional.ofNullable(dto.description()).ifPresent(item::setDescription);
        Optional.ofNullable(dto.available()).ifPresent(item::setAvailable);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item", itemId));
    }
}
