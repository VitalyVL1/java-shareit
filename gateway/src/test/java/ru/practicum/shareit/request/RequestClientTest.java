package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(RequestClient.class)
class RequestClientTest {

    @Autowired
    private RequestClient requestClient;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private MockRestServiceServer mockServer;

    private final String baseUrl = "http://localhost:9090/requests";
    private final long userId = 1L;
    private final long requestId = 1L;
    private ItemRequestCreateDto itemRequestCreateDto;

    @BeforeEach
    void setUp() {
        itemRequestCreateDto = new ItemRequestCreateDto("Description");
    }

    @Test
    void addRequest_shouldMakeCorrectPostRequest() {
        mockServer.expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value(itemRequestCreateDto.description()))
                .andRespond(withSuccess("{\"id\": 1}", MediaType.APPLICATION_JSON));

        var response = requestClient.addRequest(userId, itemRequestCreateDto);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void getRequestsByUserId_shouldMakeCorrectGetRequest() {
        mockServer.expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withSuccess("[{}]", MediaType.APPLICATION_JSON));

        var response = requestClient.getRequestsByUserId(userId);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void getRequestsById_shouldMakeCorrectGetRequest() {
        String expectedUrl = baseUrl + "/" + requestId;

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        var response = requestClient.getRequestsById(requestId);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void getAllRequests_shouldMakeCorrectGetRequest() {
        String expectedUrl = baseUrl + "/all";

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{}]", MediaType.APPLICATION_JSON));

        var response = requestClient.getAllRequests();

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void whenServerReturnsErrorOnAddRequest_shouldHandleErrorResponse() {
        mockServer.expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        var response = requestClient.addRequest(userId, itemRequestCreateDto);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void whenServerReturnsNotFoundOnGetRequestById_shouldHandle404Response() {
        String expectedUrl = baseUrl + "/" + requestId;

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        var response = requestClient.getRequestsById(requestId);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void whenServerReturnsServerErrorOnGetRequestsByUser_shouldHandle500Response() {
        mockServer.expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withServerError());

        var response = requestClient.getRequestsByUserId(userId);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void addRequest_withEmptyDescription_shouldMakeRequest() {
        ItemRequestCreateDto emptyDescriptionDto = new ItemRequestCreateDto("");

        mockServer.expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value(""))
                .andRespond(withSuccess("{\"id\": 1}", MediaType.APPLICATION_JSON));

        var response = requestClient.addRequest(userId, emptyDescriptionDto);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void getRequestsById_withDifferentIds_shouldMakeCorrectRequests() {
        long[] requestIds = {1L, 2L, 100L};

        for (long id : requestIds) {
            String expectedUrl = baseUrl + "/" + id;

            mockServer.expect(requestTo(expectedUrl))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withSuccess("{\"id\": " + id + "}", MediaType.APPLICATION_JSON));

            var response = requestClient.getRequestsById(id);

            mockServer.verify();
            assertNotNull(response);

            mockServer.reset();
        }
    }

    @Test
    void getAllRequests_withEmptyResponse_shouldHandleCorrectly() {
        String expectedUrl = baseUrl + "/all";

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        var response = requestClient.getAllRequests();

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void getRequestsByUserId_withDifferentUsers_shouldMakeCorrectRequests() {
        long[] userIds = {1L, 2L, 3L};

        for (long id : userIds) {
            mockServer.expect(requestTo(baseUrl))
                    .andExpect(method(HttpMethod.GET))
                    .andExpect(header("X-Sharer-User-Id", String.valueOf(id)))
                    .andRespond(withSuccess("[{\"id\": " + id + "}]", MediaType.APPLICATION_JSON));

            var response = requestClient.getRequestsByUserId(id);

            mockServer.verify();
            assertNotNull(response);

            mockServer.reset();
        }
    }

    @Test
    void whenServerReturnsForbiddenOnAddRequest_shouldHandle403Response() {
        mockServer.expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.FORBIDDEN));

        var response = requestClient.addRequest(userId, itemRequestCreateDto);

        mockServer.verify();
        assertNotNull(response);
    }
}