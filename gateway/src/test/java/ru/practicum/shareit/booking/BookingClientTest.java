package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.State;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(BookingClient.class)
class BookingClientTest {

    @Autowired
    private BookingClient bookingClient;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private MockRestServiceServer mockServer;

    private final String baseUrl = "http://localhost:9090/bookings";
    private final long userId = 1L;
    private final long bookingId = 1L;
    private final long ownerId = 2L;
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
    void getBookingsByBooker_shouldMakeCorrectRequest() {
        State state = State.ALL;
        Integer from = 0;
        Integer size = 10;
        String expectedUrl = baseUrl + "?state=ALL&from=0&size=10";

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        var response = bookingClient.getBookingsByBooker(userId, state, from, size);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void bookItem_shouldMakeCorrectPostRequest() {
        mockServer.expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.itemId").value(bookingCreateDto.itemId()))
                .andRespond(withSuccess("{\"id\": 1}", MediaType.APPLICATION_JSON));

        var response = bookingClient.bookItem(userId, bookingCreateDto);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void getBooking_shouldMakeCorrectGetRequest() {
        String expectedUrl = baseUrl + "/" + bookingId;

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withSuccess("{\"id\": 1}", MediaType.APPLICATION_JSON));

        var response = bookingClient.getBooking(userId, bookingId);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void approveBooking_shouldMakeCorrectPatchRequest() {
        Boolean approved = true;
        String expectedUrl = baseUrl + "/" + bookingId + "?approved=true";

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andRespond(withSuccess("{\"status\": \"APPROVED\"}", MediaType.APPLICATION_JSON));

        var response = bookingClient.approveBooking(ownerId, bookingId, approved);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void approveBooking_withFalse_shouldIncludeCorrectParameter() {
        Boolean approved = false;
        String expectedUrl = baseUrl + "/" + bookingId + "?approved=false";

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(ownerId)))
                .andRespond(withSuccess("{\"status\": \"REJECTED\"}", MediaType.APPLICATION_JSON));

        var response = bookingClient.approveBooking(ownerId, bookingId, approved);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void getBookingsByOwner_shouldMakeCorrectRequest() {
        State state = State.FUTURE;
        Integer from = 5;
        Integer size = 20;
        String expectedUrl = baseUrl + "/owner?state=FUTURE&from=5&size=20";

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        var response = bookingClient.getBookingsByOwner(userId, state, from, size);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void getBookingsByOwner_withWaitingState_shouldEncodeCorrectly() {
        State state = State.WAITING;
        String expectedUrl = baseUrl + "/owner?state=WAITING&from=0&size=10";

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        var response = bookingClient.getBookingsByOwner(userId, state, 0, 10);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void whenServerReturnsError_shouldHandleErrorResponse() {
        mockServer.expect(requestTo(baseUrl + "/" + bookingId))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        var response = bookingClient.getBooking(userId, bookingId);

        mockServer.verify();
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void whenServerReturnsServerError_shouldHandle500Response() {
        mockServer.expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withServerError());

        var response = bookingClient.bookItem(userId, bookingCreateDto);

        mockServer.verify();
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testAllStateValues() {
        for (State state : State.values()) {
            String expectedUrl = baseUrl + "?state=" + state.name() + "&from=0&size=10";

            mockServer.expect(requestTo(expectedUrl))
                    .andExpect(method(HttpMethod.GET))
                    .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                    .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

            var response = bookingClient.getBookingsByBooker(userId, state, 0, 10);

            mockServer.verify();
            assertNotNull(response);

            mockServer.reset();
        }
    }
}