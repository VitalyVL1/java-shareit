package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RequestClient requestClient;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequestCreateDto requestCreateDto;
    private final Long userId = 1L;
    private final Long requestId = 1L;

    @BeforeEach
    void setUp() {
        requestCreateDto = new ItemRequestCreateDto("Description");

    }

    @Test
    void addRequest_shouldReturnOk() throws Exception {
        when(requestClient.addRequest(anyLong(), any(ItemRequestCreateDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCreateDto)))
                .andExpect(status().isOk());

        verify(requestClient).addRequest(eq(userId), any(ItemRequestCreateDto.class));
    }

    @Test
    void addRequest_withInvalidBody_shouldReturnBadRequest() throws Exception {
        ItemRequestCreateDto invalidDto = new ItemRequestCreateDto(null);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).addRequest(anyLong(), any(ItemRequestCreateDto.class));
    }

    @Test
    void addRequest_withInvalidUserId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCreateDto)))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).addRequest(anyLong(), any(ItemRequestCreateDto.class));
    }

    @Test
    void addRequest_withMissingHeader_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCreateDto)))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).addRequest(anyLong(), any(ItemRequestCreateDto.class));
    }

    @Test
    void getRequestsByUserId_shouldReturnOk() throws Exception {
        when(requestClient.getRequestsByUserId(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(requestClient).getRequestsByUserId(eq(userId));
    }

    @Test
    void getRequestsByUserId_withInvalidUserId_shouldReturnBadRequest() throws Exception {
        when(requestClient.getRequestsByUserId(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).getRequestsByUserId(eq(userId));
    }

    @Test
    void getRequestsByUserId_withMissingHeader_shouldReturnBadRequest() throws Exception {
        when(requestClient.getRequestsByUserId(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).getRequestsByUserId(eq(userId));
    }

    @Test
    void getRequestsById_shouldReturnOk() throws Exception {
        when(requestClient.getRequestsById(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk());

        verify(requestClient).getRequestsById(eq(requestId));
    }

    @Test
    void getRequestsById_withInvalidRequestId_shouldReturnBadRequest() throws Exception {
        when(requestClient.getRequestsById(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/{requestId}", 0))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).getRequestsById(eq(requestId));
    }

    @Test
    void getAllRequests_shouldReturnOk() throws Exception {
        when(requestClient.getRequestsById(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk());

        verify(requestClient).getAllRequests();
    }
}