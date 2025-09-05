package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.item.dto.CommentCreateOrUpdateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(ItemClient.class)
class ItemClientTest {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private MockRestServiceServer mockServer;

    private final String baseUrl = "http://localhost:9090/items";
    private final long userId = 1L;
    private final long itemId = 1L;
    private final long authorId = 2L;
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
    void addItem_shouldMakeCorrectPostRequest() {
        mockServer.expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(itemCreateDto.name()))
                .andExpect(jsonPath("$.description").value(itemCreateDto.description()))
                .andExpect(jsonPath("$.available").value(itemCreateDto.available()))
                .andRespond(withSuccess("{\"id\": 1}", MediaType.APPLICATION_JSON));

        var response = itemClient.addItem(userId, itemCreateDto);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void updateItem_shouldMakeCorrectPatchRequest() {
        String expectedUrl = baseUrl + "/" + itemId;

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(itemUpdateDto.name()))
                .andRespond(withSuccess("{\"id\": 1}", MediaType.APPLICATION_JSON));

        var response = itemClient.updateItem(userId, itemId, itemUpdateDto);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void getItemsByOwner_shouldMakeCorrectGetRequest() {
        mockServer.expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withSuccess("[{}]", MediaType.APPLICATION_JSON));

        var response = itemClient.getItemsByOwner(userId);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void getItemById_shouldMakeCorrectGetRequest() {
        String expectedUrl = baseUrl + "/" + itemId;

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withSuccess("{\"id\": 1}", MediaType.APPLICATION_JSON));

        var response = itemClient.getItemById(userId, itemId);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void searchItems_shouldMakeCorrectGetRequest() {
        String searchText = "test";
        String expectedUrl = baseUrl + "/search?text=" + searchText;

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{}]", MediaType.APPLICATION_JSON));

        var response = itemClient.searchItems(searchText);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void searchItems_withEmptyText_shouldMakeCorrectRequest() {
        String searchText = "";
        String expectedUrl = baseUrl + "/search?text=";

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{}]", MediaType.APPLICATION_JSON));

        var response = itemClient.searchItems(searchText);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void searchItems_withEncodedText_shouldEncodeCorrectly() {
        String searchText = "test search";
        String expectedUrl = baseUrl + "/search?text=test%20search";

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{}]", MediaType.APPLICATION_JSON));

        var response = itemClient.searchItems(searchText);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void addComment_shouldMakeCorrectPostRequest() {
        String expectedUrl = baseUrl + "/" + itemId + "/comment";

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(authorId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value(commentCreateDto.text()))
                .andRespond(withSuccess("{\"id\": 1}", MediaType.APPLICATION_JSON));

        var response = itemClient.addComment(authorId, itemId, commentCreateDto);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void whenServerReturnsError_shouldHandleErrorResponse() {
        mockServer.expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        var response = itemClient.addItem(userId, itemCreateDto);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void whenServerReturnsNotFound_shouldHandle404Response() {
        String expectedUrl = baseUrl + "/" + itemId;

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        var response = itemClient.getItemById(userId, itemId);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void whenServerReturnsServerError_shouldHandle500Response() {
        mockServer.expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withServerError());

        var response = itemClient.getItemsByOwner(userId);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void updateItem_withPartialData_shouldSendOnlyProvidedFields() {
        ItemUpdateDto partialUpdate = ItemUpdateDto.builder()
                .name("Only Name Updated")
                .build();

        String expectedUrl = baseUrl + "/" + itemId;

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Only Name Updated"))
                .andExpect(jsonPath("$.description").doesNotExist())
                .andExpect(jsonPath("$.available").doesNotExist())
                .andRespond(withSuccess("{\"id\": 1}", MediaType.APPLICATION_JSON));

        var response = itemClient.updateItem(userId, itemId, partialUpdate);

        mockServer.verify();
        assertNotNull(response);
    }
}