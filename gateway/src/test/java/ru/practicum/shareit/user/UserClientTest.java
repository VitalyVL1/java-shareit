package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(UserClient.class)
class UserClientTest {

    @Autowired
    private UserClient userClient;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private MockRestServiceServer mockServer;

    private final String baseUrl = "http://localhost:9090/users";
    private final long userId = 1L;
    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp() {
        userCreateDto = UserCreateDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        userUpdateDto = UserUpdateDto.builder()
                .name("John Updated")
                .email("john.updated@example.com")
                .build();
    }

    @Test
    void addUser_shouldMakeCorrectPostRequest() {
        mockServer.expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(userCreateDto.name()))
                .andExpect(jsonPath("$.email").value(userCreateDto.email()))
                .andRespond(withSuccess("{\"id\": 1}", MediaType.APPLICATION_JSON));

        var response = userClient.addUser(userCreateDto);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void updateUser_shouldMakeCorrectPatchRequest() {
        String expectedUrl = baseUrl + "/" + userId;

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(userUpdateDto.name()))
                .andExpect(jsonPath("$.email").value(userUpdateDto.email()))
                .andRespond(withSuccess("{\"id\": 1}", MediaType.APPLICATION_JSON));

        var response = userClient.updateUser(userId, userUpdateDto);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void updateUser_withPartialData_shouldSendOnlyProvidedFields() {
        UserUpdateDto partialUpdate = UserUpdateDto.builder()
                .name("Only Name Updated")
                .build();

        String expectedUrl = baseUrl + "/" + userId;

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Only Name Updated"))
                .andExpect(jsonPath("$.email").doesNotExist())
                .andRespond(withSuccess("{\"id\": 1}", MediaType.APPLICATION_JSON));

        var response = userClient.updateUser(userId, partialUpdate);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void getAllUsers_shouldMakeCorrectGetRequest() {
        mockServer.expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{\"id\": 1}]", MediaType.APPLICATION_JSON));

        var response = userClient.getAllUsers();

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void getUserById_shouldMakeCorrectGetRequest() {
        String expectedUrl = baseUrl + "/" + userId;

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"id\": 1}", MediaType.APPLICATION_JSON));

        var response = userClient.getUserById(userId);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void deleteUserById_shouldMakeCorrectDeleteRequest() {
        String expectedUrl = baseUrl + "/" + userId;

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        var response = userClient.deleteUserById(userId);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void whenServerReturnsErrorOnAddUser_shouldHandleErrorResponse() {
        mockServer.expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        var response = userClient.addUser(userCreateDto);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void whenServerReturnsNotFoundOnGetUser_shouldHandle404Response() {
        String expectedUrl = baseUrl + "/" + userId;

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        var response = userClient.getUserById(userId);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void whenServerReturnsServerErrorOnUpdate_shouldHandle500Response() {
        String expectedUrl = baseUrl + "/" + userId;

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withServerError());

        var response = userClient.updateUser(userId, userUpdateDto);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void whenServerReturnsConflictOnAddUser_shouldHandle409Response() {
        mockServer.expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.CONFLICT));

        var response = userClient.addUser(userCreateDto);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void addUser_withMinimalData_shouldMakeCorrectRequest() {
        UserCreateDto minimalDto = UserCreateDto.builder()
                .email("minimal@example.com")
                .build();

        mockServer.expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("minimal@example.com"))
                .andExpect(jsonPath("$.name").doesNotExist())
                .andRespond(withSuccess("{\"id\": 1}", MediaType.APPLICATION_JSON));

        var response = userClient.addUser(minimalDto);

        mockServer.verify();
        assertNotNull(response);
    }

    @Test
    void updateUser_withOnlyEmail_shouldSendOnlyEmail() {
        UserUpdateDto emailOnlyUpdate = UserUpdateDto.builder()
                .email("only.email@example.com")
                .build();

        String expectedUrl = baseUrl + "/" + userId;

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("only.email@example.com"))
                .andExpect(jsonPath("$.name").doesNotExist())
                .andRespond(withSuccess("{\"id\": 1}", MediaType.APPLICATION_JSON));

        var response = userClient.updateUser(userId, emailOnlyUpdate);

        mockServer.verify();
        assertNotNull(response);
    }
}