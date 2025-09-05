package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        userResponseDto = UserResponseDto.builder()
                .id(1L)
                .name("name")
                .email("email@email.com")
                .build();
    }

    @Test
    void addUser() throws Exception {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("name")
                .email("email@email.com")
                .build();
        when(userService.save(any(UserCreateDto.class))).thenReturn(userResponseDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userResponseDto.id()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponseDto.name()), String.class))
                .andExpect(jsonPath("$.email", is(userResponseDto.email()), String.class));
    }

    @Test
    void updateUser() throws Exception {
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("name")
                .email("email@email.com")
                .build();
        when(userService.update(anyLong(), any(UserUpdateDto.class))).thenReturn(userResponseDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userUpdateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userResponseDto.id()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponseDto.name()), String.class))
                .andExpect(jsonPath("$.email", is(userResponseDto.email()), String.class));
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(userResponseDto));
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userResponseDto.id()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userResponseDto.name()), String.class))
                .andExpect(jsonPath("$[0].email", is(userResponseDto.email()), String.class));
    }

    @Test
    void getUserById() throws Exception {
        when(userService.findById(anyLong())).thenReturn(userResponseDto);
        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userResponseDto.id()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponseDto.name()), String.class))
                .andExpect(jsonPath("$.email", is(userResponseDto.email()), String.class));

    }

    @Test
    void deleteUserById() throws Exception {
        doNothing().when(userService).deleteById(anyLong());

        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).deleteById(1L);
    }
}