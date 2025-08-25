package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private ItemResponseDto itemResponseDto;
    private CommentRequestDto commentRequestDto;

    @BeforeEach
    void setUp() {
        commentRequestDto = CommentRequestDto.builder()
                .id(1L)
                .text("text")
                .authorName("authorName")
                .created(LocalDateTime.of(2025, 7, 7, 15, 15, 15))
                .build();

        itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();
    }

    @Test
    void addItem() throws Exception {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();

        when(itemService.save(anyLong(), any(ItemCreateDto.class))).thenReturn(itemResponseDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemResponseDto.id()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponseDto.name()), String.class))
                .andExpect(jsonPath("$.description", is(itemResponseDto.description()), String.class))
                .andExpect(jsonPath("$.available", is(itemResponseDto.available()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemResponseDto.requestId()), Long.class));
    }

    @Test
    void updateItem() throws Exception {
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        when(itemService.update(any(UpdateItemCommand.class))).thenReturn(itemResponseDto);
        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemUpdateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponseDto.id()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponseDto.name()), String.class))
                .andExpect(jsonPath("$.description", is(itemResponseDto.description()), String.class))
                .andExpect(jsonPath("$.available", is(itemResponseDto.available()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemResponseDto.requestId()), Long.class));
    }

    @Test
    void getItemsByOwner() throws Exception {
        when(itemService.findByUserId(anyLong())).thenReturn(List.of(itemResponseDto));
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemResponseDto.id()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemResponseDto.name()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemResponseDto.description()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemResponseDto.available()), Boolean.class))
                .andExpect(jsonPath("$[0].requestId", is(itemResponseDto.requestId()), Long.class));
    }

    @Test
    void getItemById() throws Exception {
        ItemResponseWithCommentsDto itemWithCommentsDto =
                ItemResponseWithCommentsDto.builder()
                        .id(1L)
                        .name("name")
                        .description("description")
                        .available(true)
                        .requestId(1L)
                        .comments(List.of(commentRequestDto))
                        .build();

        when(itemService.findById(anyLong(), anyLong())).thenReturn(itemWithCommentsDto);
        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemWithCommentsDto.id()), Long.class))
                .andExpect(jsonPath("$.name", is(itemWithCommentsDto.name()), String.class))
                .andExpect(jsonPath("$.description", is(itemWithCommentsDto.description()), String.class))
                .andExpect(jsonPath("$.available", is(itemWithCommentsDto.available()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemWithCommentsDto.requestId()), Long.class))
                .andExpect(jsonPath(
                        "$.comments[0].id",
                        is(itemWithCommentsDto.comments().getFirst().id()), Long.class))
                .andExpect(jsonPath(
                        "$.comments[0].authorName",
                        is(itemWithCommentsDto.comments().getFirst().authorName())))
                .andExpect(jsonPath(
                        "$.comments[0].text",
                        is(itemWithCommentsDto.comments().getFirst().text())))
                .andExpect(jsonPath(
                        "$.comments[0].created",
                        is(itemWithCommentsDto.comments()
                                .getFirst()
                                .created()
                                .toString())));
    }

    @Test
    void searchItems() throws Exception {
        when(itemService.search(any())).thenReturn(List.of(itemResponseDto));
        mvc.perform(get("/items/search")
                        .param("text", "text")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemResponseDto.id()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemResponseDto.name()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemResponseDto.description()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemResponseDto.available()), Boolean.class))
                .andExpect(jsonPath("$[0].requestId", is(itemResponseDto.requestId()), Long.class));
    }

    @Test
    void addComment() throws Exception {
        CommentCreateOrUpdateDto createOrUpdateDto = new CommentCreateOrUpdateDto("text");
        when(itemService.addComment(any(CreateCommentCommand.class))).thenReturn(commentRequestDto);
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(createOrUpdateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(commentRequestDto.id()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentRequestDto.authorName()), String.class))
                .andExpect(jsonPath("$.text", is(commentRequestDto.text())))
                .andExpect(jsonPath(
                        "$.created",
                        is(commentRequestDto.created().toString())));
    }
}