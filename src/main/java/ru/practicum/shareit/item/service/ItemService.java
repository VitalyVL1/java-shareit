package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

@Service
public interface ItemService {
    ItemResponseDto save(Long userId, ItemCreateDto dto);

    ItemResponseWithCommentsDto findById(Long itemId, Long userId);

    List<ItemResponseDto> findAll();

    List<ItemResponseDto> findByUserId(Long userId);

    List<ItemResponseDto> search(String query);

    ItemResponseDto update(UpdateItemCommand command);

    void deleteById(Long id);

    void clear();

    CommentRequestDto addComment(CreateCommentCommand command);

    CommentRequestDto updateComment(UpdateCommentCommand command);

    void deleteComment(Long commentId);
}
