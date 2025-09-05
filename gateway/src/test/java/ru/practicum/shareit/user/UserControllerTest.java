package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserClient userClient;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long userId = 1L;
    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp() {
        userCreateDto = UserCreateDto.builder()
                .email("email@email.com")
                .name("name")
                .build();

        userUpdateDto = UserUpdateDto.builder()
                .email("update@email.com")
                .name("update")
                .build();
    }

    @Test
    void addUser_shouldReturnOk() throws Exception {
        when(userClient.addUser(any(UserCreateDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isOk());

        verify(userClient).addUser(any(UserCreateDto.class));
    }

    @Test
    void addUser_withInvalidBody_shouldReturnBadRequest() throws Exception {
        UserCreateDto invalidDto = UserCreateDto.builder()
                .name(null)
                .email(null)
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).addUser(any(UserCreateDto.class));
    }

    @Test
    void updateUser_shouldReturnOk() throws Exception {
        when(userClient.updateUser(anyLong(), any(UserUpdateDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk());

        verify(userClient).updateUser(eq(userId), any(UserUpdateDto.class));
    }

    @Test
    void updateUser_withInvalidUserId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/users/{userId}", 0)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(eq(userId), any(UserUpdateDto.class));
        ;
    }

    @Test
    void getAllUsers_shouldReturnOk() throws Exception {
        when(userClient.getAllUsers())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userClient).getAllUsers();
    }

    @Test
    void getUserById_shouldReturnOk() throws Exception {
        when(userClient.getUserById(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userClient).getUserById(eq(userId));
    }

    @Test
    void getUserById_withInvalidUserId_shouldReturnBadRequest() throws Exception {
        when(userClient.getUserById(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/users/{userId}", 0))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).getUserById(eq(userId));
    }

    @Test
    void deleteUserById_shouldReturnOk() throws Exception {
        when(userClient.deleteUserById(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userClient).deleteUserById(eq(userId));
    }

}