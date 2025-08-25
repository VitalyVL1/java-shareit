package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private ItemRequestResponseDto itemRequestResponseDto;
    private ItemForRequestDto itemForRequestDto;

    @BeforeEach
    void setUp() {
        itemForRequestDto = ItemForRequestDto.builder()
                .id(1L)
                .name("Name")
                .ownerId(2L)
                .build();

        itemRequestResponseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .description("Description")
                .requestorId(1L)
                .items(Set.of(itemForRequestDto))
                .created(LocalDateTime.of(2025, 7, 7, 15, 15, 15))
                .build();
    }

    @Test
    void addRequest() throws Exception {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("Description");
        when(itemRequestService.save(anyLong(), any(ItemRequestCreateDto.class)))
                .thenReturn(itemRequestResponseDto);
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemRequestResponseDto.id()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestResponseDto.description())))
                .andExpect(jsonPath("$.requestorId", is(itemRequestResponseDto.requestorId()), Long.class))
                .andExpect(jsonPath("$.items[0].id", is(itemForRequestDto.id()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(itemForRequestDto.name())))
                .andExpect(jsonPath("$.items[0].ownerId", is(itemForRequestDto.ownerId()), Long.class))
                .andExpect(jsonPath(
                        "$.created",
                        is(itemRequestResponseDto.created().toString())));
    }

    @Test
    void getRequestsByUserId() throws Exception {
        when(itemRequestService.findByUserId(anyLong()))
                .thenReturn(List.of(itemRequestResponseDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestResponseDto.id()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestResponseDto.description())))
                .andExpect(jsonPath("$[0].requestorId", is(itemRequestResponseDto.requestorId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].id", is(itemForRequestDto.id()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemForRequestDto.name())))
                .andExpect(jsonPath("$[0].items[0].ownerId", is(itemForRequestDto.ownerId()), Long.class))
                .andExpect(jsonPath(
                        "$[0].created",
                        is(itemRequestResponseDto.created().toString())));
    }

    @Test
    void getRequestsById() throws Exception {
        when(itemRequestService.findById(anyLong()))
                .thenReturn(itemRequestResponseDto);

        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestResponseDto.id()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestResponseDto.description())))
                .andExpect(jsonPath("$.requestorId", is(itemRequestResponseDto.requestorId()), Long.class))
                .andExpect(jsonPath("$.items[0].id", is(itemForRequestDto.id()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(itemForRequestDto.name())))
                .andExpect(jsonPath("$.items[0].ownerId", is(itemForRequestDto.ownerId()), Long.class))
                .andExpect(jsonPath(
                        "$.created",
                        is(itemRequestResponseDto.created().toString())));
    }

    @Test
    void getAllRequests() throws Exception {
        when(itemRequestService.findAll())
                .thenReturn(List.of(itemRequestResponseDto));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestResponseDto.id()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestResponseDto.description())))
                .andExpect(jsonPath("$[0].requestorId", is(itemRequestResponseDto.requestorId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].id", is(itemForRequestDto.id()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemForRequestDto.name())))
                .andExpect(jsonPath("$[0].items[0].ownerId", is(itemForRequestDto.ownerId()), Long.class))
                .andExpect(jsonPath(
                        "$[0].created",
                        is(itemRequestResponseDto.created().toString())));
    }
}