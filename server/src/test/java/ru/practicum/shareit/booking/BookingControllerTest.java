package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingApproveDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

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

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private BookingResponseDto responseDto;
    private ItemShortDto itemShortDto;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        userResponseDto = UserResponseDto.builder()
                .id(1L)
                .name("UserName")
                .email("email@email.com")
                .build();

        itemShortDto = ItemShortDto.builder()
                .id(1L)
                .name("ItemName")
                .build();

        responseDto = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 7, 7, 15, 15, 15))
                .end(LocalDateTime.of(2025, 7, 7, 16, 15, 15))
                .item(itemShortDto)
                .booker(userResponseDto)
                .status(Status.APPROVED)
                .build();


    }

    @Test
    void addBooking() throws Exception {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        when(bookingService.save(anyLong(), any(BookingCreateDto.class)))
                .thenReturn(responseDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(responseDto.id()), Long.class))
                .andExpect(jsonPath("$.start", is(responseDto.start().toString())))
                .andExpect(jsonPath("$.end", is(responseDto.end().toString())))
                .andExpect(jsonPath("$.status", is(responseDto.status().toString())))
                .andExpect(jsonPath("$.item.id", is(responseDto.item().id()), Long.class))
                .andExpect(jsonPath("$.item.name", is(responseDto.item().name())))
                .andExpect(jsonPath("$.booker.id", is(responseDto.booker().id()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(responseDto.booker().name())));
    }

    @Test
    void approveBooking() throws Exception {
        BookingResponseDto approvedResponse = BookingResponseDto.builder()
                .id(1L)
                .start(responseDto.start())
                .end(responseDto.end())
                .item(itemShortDto)
                .booker(userResponseDto)
                .status(Status.APPROVED)
                .build();

        when(bookingService.approve(any(BookingApproveDto.class)))
                .thenReturn(approvedResponse);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 2L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(approvedResponse.id()), Long.class))
                .andExpect(jsonPath("$.status", is(approvedResponse.status().toString())));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.findById(anyLong(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.id()), Long.class))
                .andExpect(jsonPath("$.item.id", is(responseDto.item().id()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(responseDto.booker().id()), Long.class));
    }

    @Test
    void getBookingsByBookerAndState() throws Exception {
        List<BookingResponseDto> bookings = List.of(responseDto);

        when(bookingService.findByBookerIdAndState(anyLong(), any(State.class)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].id", is(responseDto.id()), Long.class))
                .andExpect(jsonPath("$[0].status", is(responseDto.status().toString())));
    }

    @Test
    void getBookingsByBookerAndState_withDefaultParams() throws Exception {
        List<BookingResponseDto> bookings = List.of(responseDto);

        when(bookingService.findByBookerIdAndState(anyLong(), any(State.class)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void getBookingsByOwnerAndState() throws Exception {
        List<BookingResponseDto> bookings = List.of(responseDto);

        when(bookingService.findByOwnerIdAndState(anyLong(), any(State.class)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 2L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].id", is(responseDto.id()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(responseDto.item().id()), Long.class));
    }

    @Test
    void getBookingsByOwnerAndState_withDefaultParams() throws Exception {
        List<BookingResponseDto> bookings = List.of(responseDto);

        when(bookingService.findByOwnerIdAndState(anyLong(), any(State.class)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void getBookingsByOwnerAndState_withDifferentStates() throws Exception {
        List<BookingResponseDto> bookings = List.of(responseDto);

        when(bookingService.findByOwnerIdAndState(anyLong(), any(State.class)))
                .thenReturn(bookings);

        for (State state : State.values()) {
            mvc.perform(get("/bookings/owner")
                            .header("X-Sharer-User-Id", 2L)
                            .param("state", state.toString())
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }
}