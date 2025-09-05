package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(UserServiceImpl.class)
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Test
    void save_ShouldSaveUserToDatabase() {
        UserCreateDto createDto = new UserCreateDto("Test User", "test@email.com");

        UserResponseDto savedUser = userService.save(createDto);

        assertNotNull(savedUser.id());
        assertEquals(createDto.name(), savedUser.name());
        assertEquals(createDto.email(), savedUser.email());

        UserResponseDto foundUser = userService.findById(savedUser.id());
        assertEquals(savedUser.id(), foundUser.id());
        assertEquals(savedUser.name(), foundUser.name());
        assertEquals(savedUser.email(), foundUser.email());
    }

    @Test
    void save_ShouldThrowDuplicatedDataException_WhenEmailAlreadyExists() {
        UserCreateDto firstUser = new UserCreateDto("First User", "duplicate@email.com");
        userService.save(firstUser);

        UserCreateDto secondUser = new UserCreateDto("Second User", "duplicate@email.com");

        DuplicatedDataException exception = assertThrows(DuplicatedDataException.class,
                () -> userService.save(secondUser));

        assertEquals("email", exception.getFieldName());
        assertEquals(secondUser.email(), exception.getDuplicatedValue());
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        UserCreateDto createDto = new UserCreateDto("Test User", "test@email.com");
        UserResponseDto savedUser = userService.save(createDto);

        UserResponseDto foundUser = userService.findById(savedUser.id());

        assertNotNull(foundUser);
        assertEquals(savedUser.id(), foundUser.id());
        assertEquals(savedUser.name(), foundUser.name());
        assertEquals(savedUser.email(), foundUser.email());
    }

    @Test
    void findById_ShouldThrowNotFoundException_WhenUserNotExists() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.findById(999L));

        assertEquals("User", exception.getEntityName());
        assertEquals(999L, exception.getEntityId());
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        UserCreateDto user1 = new UserCreateDto("User 1", "user1@email.com");
        UserCreateDto user2 = new UserCreateDto("User 2", "user2@email.com");
        UserCreateDto user3 = new UserCreateDto("User 3", "user3@email.com");

        userService.save(user1);
        userService.save(user2);
        userService.save(user3);

        List<UserResponseDto> users = userService.findAll();

        assertEquals(3, users.size());
        assertTrue(users.stream().anyMatch(u -> u.email().equals(user1.email())));
        assertTrue(users.stream().anyMatch(u -> u.email().equals(user2.email())));
        assertTrue(users.stream().anyMatch(u -> u.email().equals(user3.email())));
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoUsers() {
        // When
        List<UserResponseDto> users = userService.findAll();

        // Then
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void update_ShouldUpdateUserInDatabase() {
        UserCreateDto createDto = new UserCreateDto("Original Name", "original@email.com");
        UserResponseDto originalUser = userService.save(createDto);

        UserUpdateDto updateDto = new UserUpdateDto("Updated Name", "updated@email.com");

        UserResponseDto updatedUser = userService.update(originalUser.id(), updateDto);

        assertEquals(originalUser.id(), updatedUser.id());
        assertEquals(updateDto.name(), updatedUser.name());
        assertEquals(updateDto.email(), updatedUser.email());

        UserResponseDto foundUser = userService.findById(originalUser.id());
        assertEquals(updateDto.name(), foundUser.name());
        assertEquals(updateDto.email(), foundUser.email());
    }

    @Test
    void update_ShouldUpdateOnlyName_WhenEmailIsNull() {
        UserCreateDto createDto = new UserCreateDto("Original Name", "original@email.com");
        UserResponseDto originalUser = userService.save(createDto);

        UserUpdateDto updateDto = new UserUpdateDto("Updated Name", null);

        UserResponseDto updatedUser = userService.update(originalUser.id(), updateDto);

        assertEquals(originalUser.id(), updatedUser.id());
        assertEquals(updateDto.name(), updatedUser.name());
        assertEquals(createDto.email(), updatedUser.email());
    }

    @Test
    void update_ShouldUpdateOnlyEmail_WhenNameIsNull() {
        UserCreateDto createDto = new UserCreateDto("Original Name", "original@email.com");
        UserResponseDto originalUser = userService.save(createDto);

        UserUpdateDto updateDto = new UserUpdateDto(null, "updated@email.com");

        UserResponseDto updatedUser = userService.update(originalUser.id(), updateDto);

        assertEquals(originalUser.id(), updatedUser.id());
        assertEquals(createDto.name(), updatedUser.name());
        assertEquals(updateDto.email(), updatedUser.email());
    }

    @Test
    void update_ShouldThrowDuplicatedDataException_WhenEmailAlreadyExists() {
        UserCreateDto firstUser = new UserCreateDto("First User", "first@email.com");
        UserCreateDto secondUser = new UserCreateDto("Second User", "second@email.com");

        UserResponseDto savedFirst = userService.save(firstUser);
        userService.save(secondUser);

        UserUpdateDto updateDto = new UserUpdateDto("Updated Name", "second@email.com");

        DuplicatedDataException exception = assertThrows(DuplicatedDataException.class,
                () -> userService.update(savedFirst.id(), updateDto));

        assertEquals("email", exception.getFieldName());
        assertEquals(updateDto.email(), exception.getDuplicatedValue());
    }

    @Test
    void update_ShouldNotThrow_WhenEmailNotChanged() {
        UserCreateDto createDto = new UserCreateDto("Original Name", "original@email.com");
        UserResponseDto originalUser = userService.save(createDto);

        UserUpdateDto updateDto = new UserUpdateDto("Updated Name", "original@email.com");

        assertDoesNotThrow(() -> {
            UserResponseDto updatedUser = userService.update(originalUser.id(), updateDto);
            assertEquals(updateDto.name(), updatedUser.name());
            assertEquals(updateDto.email(), updatedUser.email());
        });
    }

    @Test
    void deleteById_ShouldRemoveUserFromDatabase() {
        UserCreateDto createDto = new UserCreateDto("Test User", "test@email.com");
        UserResponseDto savedUser = userService.save(createDto);

        assertNotNull(userService.findById(savedUser.id()));

        userService.deleteById(savedUser.id());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.findById(savedUser.id()));

        assertEquals("User", exception.getEntityName());
        assertEquals(savedUser.id(), exception.getEntityId());
    }

    @Test
    void clear_ShouldRemoveAllUsersFromDatabase() {
        userService.save(new UserCreateDto("User 1", "user1@email.com"));
        userService.save(new UserCreateDto("User 2", "user2@email.com"));
        userService.save(new UserCreateDto("User 3", "user3@email.com"));

        assertEquals(3, userService.findAll().size());

        userService.clear();

        List<UserResponseDto> users = userService.findAll();
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void save_ShouldHandleDatabaseConstraints() {
        UserCreateDto user = new UserCreateDto("Test User", "test@email.com");
        userService.save(user);

        UserCreateDto duplicateUser = new UserCreateDto("Duplicate User", "test@email.com");

        DuplicatedDataException exception = assertThrows(DuplicatedDataException.class,
                () -> userService.save(duplicateUser));

        assertEquals("email", exception.getFieldName());
        assertEquals(duplicateUser.email(), exception.getDuplicatedValue());
    }

    @Test
    void update_ShouldHandleConcurrentModifications() {
        UserCreateDto createDto = new UserCreateDto("Original Name", "original@email.com");
        UserResponseDto originalUser = userService.save(createDto);

        UserUpdateDto firstUpdate = new UserUpdateDto("First Update", "first@email.com");
        UserResponseDto firstResult = userService.update(originalUser.id(), firstUpdate);

        UserUpdateDto secondUpdate = new UserUpdateDto("Second Update", "second@email.com");
        UserResponseDto secondResult = userService.update(originalUser.id(), secondUpdate);

        UserResponseDto finalUser = userService.findById(originalUser.id());
        assertEquals(secondUpdate.name(), finalUser.name());
        assertEquals(secondUpdate.email(), finalUser.email());
    }
}