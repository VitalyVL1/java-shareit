package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentCreateOrUpdateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemClient itemClient;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long userId = 1L;
    private final Long itemId = 1L;
    private final Long authorId = 2L;
    private ItemCreateDto itemCreateDto;
    private ItemUpdateDto itemUpdateDto;
    private CommentCreateOrUpdateDto commentCreateDto;

    @BeforeEach
    void setUp() {
        itemCreateDto = ItemCreateDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        itemUpdateDto = ItemUpdateDto.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();

        commentCreateDto = new CommentCreateOrUpdateDto("Text");

    }

    @Test
    void addItem_shouldReturnOk() throws Exception {
        when(itemClient.addItem(anyLong(), any(ItemCreateDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isOk());

        verify(itemClient).addItem(eq(userId), any(ItemCreateDto.class));
    }

    @Test
    void addItem_withInvalidBody_shouldReturnBadRequest() throws Exception {
        ItemCreateDto invalidDto = ItemCreateDto.builder()
                .name(null)
                .description(null)
                .available(null)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addItem(anyLong(), any(ItemCreateDto.class));
    }

    @Test
    void addItem_withInvalidUserId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addItem(anyLong(), any(ItemCreateDto.class));
    }

    @Test
    void addItem_withMissingHeader_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addItem(anyLong(), any(ItemCreateDto.class));
    }

    @Test
    void updateItem_shouldReturnOk() throws Exception {
        when(itemClient.updateItem(anyLong(), anyLong(), any(ItemUpdateDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isOk());

        verify(itemClient).updateItem(eq(userId), eq(itemId), any(ItemUpdateDto.class));
    }

    @Test
    void updateItem_withInvalidItemId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/items/{itemId}", 0)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).updateItem(anyLong(), anyLong(), any(ItemUpdateDto.class));
    }

    @Test
    void updateItem_withInvalidUserId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).updateItem(anyLong(), anyLong(), any(ItemUpdateDto.class));
    }

    @Test
    void getItemsByOwner_shouldReturnOk() throws Exception {
        when(itemClient.getItemsByOwner(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemClient).getItemsByOwner(eq(userId));
    }

    @Test
    void getItemsByOwner_withInvalidUserId_shouldReturnBadRequest() throws Exception {
        when(itemClient.getItemsByOwner(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItemsByOwner(eq(userId));
    }

    @Test
    void getItemById_withInvalidUserId_shouldReturnBadRequest() throws Exception {
        when(itemClient.getItemById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItemById(eq(userId), eq(itemId));
    }

    @Test
    void getItemById_withInvalidItemId_shouldReturnBadRequest() throws Exception {
        when(itemClient.getItemById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/{itemId}", 0)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItemById(eq(userId), eq(itemId));
    }

    @Test
    void getItemById_shouldReturnOk() throws Exception {
        when(itemClient.getItemById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemClient).getItemById(eq(userId), eq(itemId));
    }

    @Test
    void searchItems_shouldReturnOk() throws Exception {
        when(itemClient.searchItems(anyString()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/search")
                        .param("text", "test"))
                .andExpect(status().isOk());

        verify(itemClient).searchItems(eq("test"));
    }

    @Test
    void addComment_shouldReturnOk() throws Exception {
        when(itemClient.addComment(anyLong(), anyLong(), any(CommentCreateOrUpdateDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", authorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isOk());

        verify(itemClient).addComment(eq(authorId), eq(itemId), any(CommentCreateOrUpdateDto.class));
    }
}