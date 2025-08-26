package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.State;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingClient bookingClient;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long userId = 1L;
    private final Long ownerId = 2L;
    private final Long bookingId = 1L;
    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        bookingCreateDto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void addBooking_shouldReturnOk() throws Exception {
        when(bookingClient.bookItem(anyLong(), any(BookingCreateDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isOk());

        verify(bookingClient).bookItem(eq(userId), any(BookingCreateDto.class));
    }

    @Test
    void addBooking_withInvalidUserId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).bookItem(anyLong(), any(BookingCreateDto.class));
    }

    @Test
    void addBooking_withInvalidBody_shouldReturnBadRequest() throws Exception {
        BookingCreateDto invalidDto = BookingCreateDto.builder()
                .itemId(null)
                .start(null)
                .end(null)
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).bookItem(anyLong(), any(BookingCreateDto.class));
    }

    @Test
    void addBooking_withMissingHeader_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).bookItem(anyLong(), any(BookingCreateDto.class));
    }

    @Test
    void approveBooking_shouldReturnOk() throws Exception {
        when(bookingClient.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient).approveBooking(eq(ownerId), eq(bookingId), eq(true));
    }

    @Test
    void approveBooking_withInvalidParams_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", 0)
                        .header("X-Sharer-User-Id", 0)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void approveBooking_withMissingApprovedParam_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getBookingById_shouldReturnOk() throws Exception {
        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient).getBooking(eq(userId), eq(bookingId));
    }

    @Test
    void getBookingById_withInvalidBookingId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/{bookingId}", 0)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBooking(anyLong(), anyLong());
    }

    @Test
    void getBookingsByBookerAndState_shouldReturnOk() throws Exception {
        when(bookingClient.getBookingsByBooker(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "all")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(bookingClient).getBookingsByBooker(eq(userId), eq(State.ALL), eq(0), eq(10));
    }

    @Test
    void getBookingsByBookerAndState_withDefaultParams_shouldReturnOk() throws Exception {
        when(bookingClient.getBookingsByBooker(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient).getBookingsByBooker(eq(userId), eq(State.ALL), eq(0), eq(10));
    }

    @Test
    void getBookingsByBookerAndState_withInvalidFrom_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookingsByBooker(anyLong(), any(State.class), anyInt(), anyInt());
    }

    @Test
    void getBookingsByBookerAndState_withInvalidSize_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookingsByBooker(anyLong(), any(State.class), anyInt(), anyInt());
    }

    @Test
    void getBookingsByBookerAndState_withUnknownState_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "Unknown"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookingsByBooker(anyLong(), any(State.class), anyInt(), anyInt());
    }

    @Test
    void getBookingsByOwnerAndState_shouldReturnOk() throws Exception {
        when(bookingClient.getBookingsByOwner(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "current")
                        .param("from", "5")
                        .param("size", "20"))
                .andExpect(status().isOk());

        verify(bookingClient).getBookingsByOwner(eq(ownerId), eq(State.CURRENT), eq(5), eq(20));
    }

    @Test
    void getBookingsByOwnerAndState_withDefaultParams_shouldReturnOk() throws Exception {
        when(bookingClient.getBookingsByOwner(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk());

        verify(bookingClient).getBookingsByOwner(eq(ownerId), eq(State.ALL), eq(0), eq(10));
    }

    @Test
    void getBookingsByOwnerAndState_withInvalidParams_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("from", "-1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookingsByOwner(anyLong(), any(State.class), anyInt(), anyInt());
    }

    @Test
    void getBookingsByOwnerAndState_withUnknownState_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "invalid"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookingsByOwner(anyLong(), any(State.class), anyInt(), anyInt());
    }
}