package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(ItemRequestServiceImpl.class)
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User createTestUser(String name, String email) {
        User user = User.builder()
                .name(name)
                .email(email)
                .build();
        return userRepository.save(user);
    }

    private Item createTestItem(User owner, String name, String description, Boolean available) {
        Item item = Item.builder()
                .name(name)
                .description(description)
                .available(available)
                .owner(owner)
                .build();
        return itemRepository.save(item);
    }

    @Test
    void save_ShouldSaveItemRequest() {
        User requestor = createTestUser("Requestor", "requestor@email.com");
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Description");

        ItemRequestResponseDto savedRequest = itemRequestService.save(requestor.getId(), createDto);

        assertNotNull(savedRequest.id());
        assertEquals(createDto.description(), savedRequest.description());
        assertEquals(requestor.getId(), savedRequest.requestorId());
        assertTrue(savedRequest.items().isEmpty());

        ItemRequestResponseDto foundRequest = itemRequestService.findById(savedRequest.id());
        assertEquals(savedRequest.id(), foundRequest.id());
        assertEquals(savedRequest.description(), foundRequest.description());
    }

    @Test
    void save_ShouldThrowNotFoundException_WhenUserNotExists() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Description");

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.save(999L, createDto));

        assertEquals("User", exception.getEntityName());
        assertEquals(999L, exception.getEntityId());
    }

    @Test
    void findById_ShouldReturnItemRequestWithItems() {
        User requestor = createTestUser("Requestor", "requestor@email.com");
        User owner = createTestUser("Owner", "owner@email.com");

        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Description");
        ItemRequestResponseDto savedRequest = itemRequestService.save(requestor.getId(), createDto);

        Item item1 = createTestItem(owner, "Name1", "Description1", true);
        item1.setRequest(itemRequestRepository.findById(savedRequest.id()).get());
        itemRepository.save(item1);

        Item item2 = createTestItem(owner, "Name2", "Description2", true);
        item2.setRequest(itemRequestRepository.findById(savedRequest.id()).get());
        itemRepository.save(item2);

        ItemRequestResponseDto foundRequest = itemRequestService.findById(savedRequest.id());

        assertNotNull(foundRequest);
        assertEquals(savedRequest.id(), foundRequest.id());
        assertEquals(2, foundRequest.items().size());
        assertTrue(foundRequest.items().stream().anyMatch(item -> item.name().equals(item1.getName())));
        assertTrue(foundRequest.items().stream().anyMatch(item -> item.name().equals(item2.getName())));
    }

    @Test
    void findById_ShouldThrowNotFoundException_WhenRequestNotExists() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.findById(999L));

        assertEquals("ItemRequest", exception.getEntityName());
        assertEquals(999L, exception.getEntityId());
    }

    @Test
    void findAll_ShouldReturnAllItemRequestsWithItems() {
        User requestor1 = createTestUser("Requestor1", "requestor1@email.com");
        User requestor2 = createTestUser("Requestor2", "requestor2@email.com");
        User owner = createTestUser("Owner", "owner@email.com");

        ItemRequestResponseDto request1 = itemRequestService.save(requestor1.getId(),
                new ItemRequestCreateDto("Description1"));
        ItemRequestResponseDto request2 = itemRequestService.save(requestor2.getId(),
                new ItemRequestCreateDto("Description2"));

        Item item1 = createTestItem(owner, "Name1", "Description1", true);
        item1.setRequest(itemRequestRepository.findById(request1.id()).get());
        itemRepository.save(item1);

        Item item2 = createTestItem(owner, "Name2", "Description2", true);
        item2.setRequest(itemRequestRepository.findById(request2.id()).get());
        itemRepository.save(item2);

        List<ItemRequestResponseDto> allRequests = itemRequestService.findAll();

        assertEquals(2, allRequests.size());

        assertTrue(allRequests.get(0).created().isAfter(allRequests.get(1).created()) ||
                   allRequests.get(0).created().equals(allRequests.get(1).created()));

        assertEquals(1, allRequests.get(0).items().size());
        assertEquals(1, allRequests.get(1).items().size());
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoRequests() {
        List<ItemRequestResponseDto> allRequests = itemRequestService.findAll();

        assertNotNull(allRequests);
        assertTrue(allRequests.isEmpty());
    }

    @Test
    void findByUserId_ShouldReturnUserItemRequestsWithItems() {
        User requestor = createTestUser("Requestor", "requestor@email.com");
        User otherUser = createTestUser("Other", "other@email.com");
        User owner = createTestUser("Owner", "owner@email.com");

        ItemRequestResponseDto userRequest1 = itemRequestService.save(requestor.getId(),
                new ItemRequestCreateDto("User request 1"));
        ItemRequestResponseDto userRequest2 = itemRequestService.save(requestor.getId(),
                new ItemRequestCreateDto("User request 2"));
        itemRequestService.save(otherUser.getId(), new ItemRequestCreateDto("Other user request"));

        Item item1 = createTestItem(owner, "Item 1", "For request 1", true);
        item1.setRequest(itemRequestRepository.findById(userRequest1.id()).get());
        itemRepository.save(item1);

        Item item2 = createTestItem(owner, "Item 2", "For request 2", true);
        item2.setRequest(itemRequestRepository.findById(userRequest2.id()).get());
        itemRepository.save(item2);

        List<ItemRequestResponseDto> userRequests = itemRequestService.findByUserId(requestor.getId());

        assertEquals(2, userRequests.size());
        assertTrue(userRequests.stream().allMatch(req -> req.requestorId().equals(requestor.getId())));

        assertEquals(1, userRequests.get(0).items().size());
        assertEquals(1, userRequests.get(1).items().size());

        assertTrue(userRequests.get(0).created().isAfter(userRequests.get(1).created()) ||
                   userRequests.get(0).created().equals(userRequests.get(1).created()));
    }

    @Test
    void findByUserId_ShouldReturnEmptyList_WhenUserHasNoRequests() {
        User user = createTestUser("User", "user@email.com");

        List<ItemRequestResponseDto> userRequests = itemRequestService.findByUserId(user.getId());

        assertNotNull(userRequests);
        assertTrue(userRequests.isEmpty());
    }

    @Test
    void findByUserId_ShouldThrowNotFoundException_WhenUserNotExists() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.findByUserId(999L));

        assertEquals("User", exception.getEntityName());
        assertEquals(999L, exception.getEntityId());
    }

    @Test
    void deleteById_ShouldRemoveItemRequest() {
        User requestor = createTestUser("Requestor", "requestor@email.com");
        ItemRequestResponseDto savedRequest = itemRequestService.save(requestor.getId(),
                new ItemRequestCreateDto("Test request"));

        assertNotNull(itemRequestService.findById(savedRequest.id()));

        itemRequestService.deleteById(savedRequest.id());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.findById(savedRequest.id()));

        assertEquals("ItemRequest", exception.getEntityName());
        assertEquals(savedRequest.id(), exception.getEntityId());
    }

    @Test
    void clear_ShouldRemoveAllItemRequests() {
        User requestor1 = createTestUser("Requestor1", "requestor1@email.com");
        User requestor2 = createTestUser("Requestor2", "requestor2@email.com");

        itemRequestService.save(requestor1.getId(), new ItemRequestCreateDto("Request 1"));
        itemRequestService.save(requestor2.getId(), new ItemRequestCreateDto("Request 2"));

        assertEquals(2, itemRequestService.findAll().size());

        itemRequestService.clear();

        List<ItemRequestResponseDto> allRequests = itemRequestService.findAll();
        assertNotNull(allRequests);
        assertTrue(allRequests.isEmpty());
    }

    @Test
    void findByUserId_ShouldNotIncludeOtherUsersRequests() {
        User user1 = createTestUser("User1", "user1@email.com");
        User user2 = createTestUser("User2", "user2@email.com");

        itemRequestService.save(user1.getId(), new ItemRequestCreateDto("User1 request"));
        itemRequestService.save(user2.getId(), new ItemRequestCreateDto("User2 request"));

        List<ItemRequestResponseDto> user1Requests = itemRequestService.findByUserId(user1.getId());
        List<ItemRequestResponseDto> user2Requests = itemRequestService.findByUserId(user2.getId());

        assertEquals(1, user1Requests.size());
        assertEquals(1, user2Requests.size());
        assertEquals(user1.getId(), user1Requests.get(0).requestorId());
        assertEquals(user2.getId(), user2Requests.get(0).requestorId());
    }

    @Test
    void findAll_ShouldHandleRequestsWithoutItems() {
        User requestor = createTestUser("Requestor", "requestor@email.com");
        itemRequestService.save(requestor.getId(), new ItemRequestCreateDto("Request without items"));

        List<ItemRequestResponseDto> allRequests = itemRequestService.findAll();

        assertEquals(1, allRequests.size());
        assertTrue(allRequests.get(0).items().isEmpty());
    }
}