package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.AccessForbiddenException;
import ru.practicum.shareit.exception.CommentNotAllowedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(ItemServiceImpl.class)
class ItemServiceImplTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User createUser(String name, String email) {
        User user = User.builder()
                .name(name)
                .email(email)
                .build();
        return userRepository.save(user);
    }

    private Item createItem(User owner, String name, String description, Boolean available, ItemRequest request) {
        Item item = Item.builder()
                .name(name)
                .description(description)
                .available(available)
                .owner(owner)
                .request(request)
                .build();
        return itemRepository.save(item);
    }

    private Booking createBooking(User booker, Item item, LocalDateTime start, LocalDateTime end, Status status) {
        Booking booking = Booking.builder()
                .booker(booker)
                .item(item)
                .start(start)
                .end(end)
                .status(status)
                .build();
        return bookingRepository.save(booking);
    }

    private Comment createComment(User author, Item item, String text) {
        Comment comment = Comment.builder()
                .author(author)
                .item(item)
                .text(text)
                .created(LocalDateTime.now())
                .build();
        return commentRepository.save(comment);
    }

    private ItemRequest createItemRequest(User requestor, String description) {
        ItemRequest request = ItemRequest.builder()
                .description(description)
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
        return itemRequestRepository.save(request);
    }

    @Test
    void save_ShouldSaveItemWithoutRequest() {
        User owner = createUser("Owner", "owner@email.com");
        ItemCreateDto createDto = new ItemCreateDto("Name", "Description", true, null);

        ItemResponseDto savedItem = itemService.save(owner.getId(), createDto);

        assertNotNull(savedItem.id());
        assertEquals(createDto.name(), savedItem.name());
        assertEquals(createDto.description(), savedItem.description());
        assertTrue(savedItem.available());
        assertNull(savedItem.requestId());

        ItemResponseWithCommentsDto foundItem = itemService.findById(savedItem.id(), owner.getId());
        assertEquals(savedItem.id(), foundItem.id());
        assertEquals(savedItem.name(), foundItem.name());
        assertEquals(savedItem.description(), foundItem.description());
    }

    @Test
    void save_ShouldSaveItemWithRequest() {
        User owner = createUser("Owner", "owner@email.com");
        User requestor = createUser("Requestor", "requestor@email.com");
        ItemRequest request = createItemRequest(requestor, "Description");

        ItemCreateDto createDto = new ItemCreateDto("Name", "Description", true, request.getId());

        ItemResponseDto savedItem = itemService.save(owner.getId(), createDto);

        assertNotNull(savedItem.id());
        assertEquals(request.getId(), savedItem.requestId());
    }

    @Test
    void save_ShouldThrowNotFoundException_WhenUserNotExists() {
        ItemCreateDto createDto = new ItemCreateDto("Name", "Description", true, null);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.save(999L, createDto));

        assertEquals("User", exception.getEntityName());
        assertEquals(999L, exception.getEntityId());
    }

    @Test
    void findById_ShouldReturnItemWithBookingsForOwner() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true, null);

        LocalDateTime now = LocalDateTime.now();
        Booking pastBooking = createBooking(booker, item, now.minusDays(2), now.minusDays(1), Status.APPROVED);
        Booking futureBooking = createBooking(booker, item, now.plusDays(1), now.plusDays(2), Status.APPROVED);

        ItemResponseWithCommentsDto foundItem = itemService.findById(item.getId(), owner.getId());

        assertNotNull(foundItem);
        assertEquals(item.getId(), foundItem.id());
        assertNotNull(foundItem.lastBooking());
        assertEquals(pastBooking.getEnd(), foundItem.lastBooking());
        assertNotNull(foundItem.nextBooking());
        assertEquals(futureBooking.getStart(), foundItem.nextBooking());
        assertTrue(foundItem.comments().isEmpty());
    }

    @Test
    void findById_ShouldReturnItemWithoutBookingsForNonOwner() {
        User owner = createUser("Owner", "owner@email.com");
        User otherUser = createUser("Other", "other@email.com");
        Item item = createItem(owner, "Name", "Description", true, null);

        LocalDateTime now = LocalDateTime.now();
        createBooking(otherUser, item, now.minusDays(2), now.minusDays(1), Status.APPROVED);
        createBooking(otherUser, item, now.plusDays(1), now.plusDays(2), Status.APPROVED);

        ItemResponseWithCommentsDto foundItem = itemService.findById(item.getId(), otherUser.getId());

        assertNotNull(foundItem);
        assertEquals(item.getId(), foundItem.id());
        assertNull(foundItem.lastBooking());
        assertNull(foundItem.nextBooking());
    }

    @Test
    void findById_ShouldReturnItemWithComments() {
        User owner = createUser("Owner", "owner@email.com");
        User commenter = createUser("Commenter", "commenter@email.com");
        Item item = createItem(owner, "Name", "Description", true, null);

        createComment(commenter, item, "comment1");
        createComment(commenter, item, "comment2");

        ItemResponseWithCommentsDto foundItem = itemService.findById(item.getId(), owner.getId());

        assertNotNull(foundItem);
        assertEquals(2, foundItem.comments().size());
    }

    @Test
    void findById_ShouldThrowNotFoundException_WhenItemNotExists() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.findById(999L, 1L));

        assertEquals("Item", exception.getEntityName());
        assertEquals(999L, exception.getEntityId());
    }

    @Test
    void findAll_ShouldReturnAllItems() {
        User owner1 = createUser("Owner1", "owner1@email.com");
        User owner2 = createUser("Owner2", "owner2@email.com");

        createItem(owner1, "Name1", "Description1", true, null);
        createItem(owner2, "Name2", "Description2", true, null);

        List<ItemResponseDto> items = itemService.findAll();

        assertEquals(2, items.size());
    }

    @Test
    void findByUserId_ShouldReturnUserItems() {
        User owner = createUser("Owner", "owner@email.com");
        User otherUser = createUser("Other", "other@email.com");

        Item item1 = createItem(owner, "OwnerItem1", "OwnerDescription1", true, null);
        Item item2 = createItem(owner, "OwnerItem2", "OwnerDescription2", true, null);
        createItem(otherUser, "OterItem", "OtherDescription", true, null);

        List<ItemResponseWithCommentsDto> userItems = itemService.findByUserId(owner.getId());

        assertEquals(2, userItems.size());
        assertTrue(userItems.stream().allMatch(item ->
                item.name().equals(item1.getName()) || item.name().equals(item2.getName())));
    }

    @Test
    void search_ShouldReturnMatchingItems() {
        User owner = createUser("Owner", "owner@email.com");
        createItem(owner, "Power Drill", "Electric tool", true, null);
        createItem(owner, "Hammer", "Hand tool", true, null);
        createItem(owner, "Broken Saw", "Not working", false, null);

        List<ItemResponseDto> drillResults = itemService.search("drill");
        List<ItemResponseDto> toolResults = itemService.search("tool");
        List<ItemResponseDto> emptyResults = itemService.search("");
        List<ItemResponseDto> unavailableResults = itemService.search("saw");

        assertEquals(1, drillResults.size());
        assertEquals("Power Drill", drillResults.get(0).name());

        assertEquals(2, toolResults.size());
        assertTrue(emptyResults.isEmpty());
        assertTrue(unavailableResults.isEmpty());
    }

    @Test
    void update_ShouldUpdateItem() {
        User owner = createUser("Owner", "owner@email.com");
        Item item = createItem(owner, "Old Name", "Old description", true, null);

        ItemUpdateDto updateData = new ItemUpdateDto("New Name", "New description", false);
        UpdateItemCommand command = new UpdateItemCommand(item.getId(), owner.getId(), updateData);

        ItemResponseDto updatedItem = itemService.update(command);

        assertEquals("New Name", updatedItem.name());
        assertEquals("New description", updatedItem.description());
        assertFalse(updatedItem.available());
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenItemNotOwnedByUser() {
        User owner = createUser("Owner", "owner@email.com");
        User otherUser = createUser("Other", "other@email.com");
        Item item = createItem(owner, "Name", "Description", true, null);

        ItemUpdateDto updateData = new ItemUpdateDto("New Name", null, null);
        UpdateItemCommand command = new UpdateItemCommand(otherUser.getId(), item.getId(), updateData);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.update(command));

        assertTrue(exception.getMessage().contains("Item not owned by user"));
    }

    @Test
    void update_ShouldUpdatePartialFields() {
        User owner = createUser("Owner", "owner@email.com");
        Item item = createItem(owner, "Name", "Description", true, null);

        // Update only name
        ItemUpdateDto nameUpdate = new ItemUpdateDto("New Name", null, null);
        ItemResponseDto updatedNameItem = itemService.update(new UpdateItemCommand(owner.getId(), item.getId(), nameUpdate));

        assertEquals(nameUpdate.name(), updatedNameItem.name());
        assertEquals(item.getDescription(), updatedNameItem.description());
        assertTrue(updatedNameItem.available());

        // Update only availability
        ItemUpdateDto availabilityUpdate = new ItemUpdateDto(null, null, false);
        ItemResponseDto updatedAvailabilityItem = itemService.update(new UpdateItemCommand(owner.getId(), item.getId(), availabilityUpdate));

        assertEquals(item.getName(), updatedAvailabilityItem.name());
        assertEquals(item.getDescription(), updatedAvailabilityItem.description());
        assertFalse(updatedAvailabilityItem.available());

        // Update only description
        ItemUpdateDto descriptionUpdate = new ItemUpdateDto(null, "New Description", null);
        ItemResponseDto updatedDescriptionItem = itemService.update(new UpdateItemCommand(owner.getId(), item.getId(), descriptionUpdate));

        assertEquals(item.getName(), updatedDescriptionItem.name());
        assertEquals(descriptionUpdate.description(), updatedDescriptionItem.description());
        assertFalse(updatedDescriptionItem.available());
    }

    @Test
    void addComment_ShouldAddComment() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true, null);

        LocalDateTime now = LocalDateTime.now();
        createBooking(booker, item, now.minusDays(2), now.minusDays(1), Status.APPROVED);

        CommentCreateOrUpdateDto commentDto = new CommentCreateOrUpdateDto("Comment");
        CreateCommentCommand command = new CreateCommentCommand(item.getId(), booker.getId(), commentDto);

        CommentRequestDto comment = itemService.addComment(command);

        assertNotNull(comment.id());
        assertEquals(commentDto.text(), comment.text());
        assertEquals(booker.getName(), comment.authorName());
        assertNotNull(comment.created());

        ItemResponseWithCommentsDto itemWithComments = itemService.findById(item.getId(), owner.getId());
        assertEquals(1, itemWithComments.comments().size());
    }

    @Test
    void addComment_ShouldThrowCommentNotAllowedException_WhenBookingNotEnded() {
        User owner = createUser("Owner", "owner@email.com");
        User booker = createUser("Booker", "booker@email.com");
        Item item = createItem(owner, "Name", "Description", true, null);

        LocalDateTime now = LocalDateTime.now();
        createBooking(booker, item, now.minusDays(1), now.plusDays(1), Status.APPROVED);

        CommentCreateOrUpdateDto commentDto = new CommentCreateOrUpdateDto("Comment");
        CreateCommentCommand command = new CreateCommentCommand(item.getId(), booker.getId(), commentDto);

        CommentNotAllowedException exception = assertThrows(CommentNotAllowedException.class,
                () -> itemService.addComment(command));

        assertTrue(exception.getMessage().contains("Comment before booking's end"));
    }

    @Test
    void addComment_ShouldThrowNotFoundException_WhenNoBooking() {
        User owner = createUser("Owner", "owner@email.com");
        User user = createUser("User", "user@email.com");
        Item item = createItem(owner, "Name", "Description", true, null);

        CommentCreateOrUpdateDto commentDto = new CommentCreateOrUpdateDto("Comment");
        CreateCommentCommand command = new CreateCommentCommand(item.getId(), user.getId(), commentDto);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(command));

        assertTrue(exception.getMessage().contains("Booking not found"));
    }

    @Test
    void updateComment_ShouldUpdateComment() {
        User owner = createUser("Owner", "owner@email.com");
        User author = createUser("Author", "author@email.com");
        Item item = createItem(owner, "Name", "Description", true, null);

        Comment comment = createComment(author, item, "Comment");

        CommentCreateOrUpdateDto updateDto = new CommentCreateOrUpdateDto("New Comment");
        UpdateCommentCommand command = new UpdateCommentCommand(comment.getId(), item.getId(), author.getId(), updateDto);

        CommentRequestDto updatedComment = itemService.updateComment(command);

        assertEquals(updateDto.text(), updatedComment.text());
        assertEquals(comment.getId(), updatedComment.id());
    }

    @Test
    void updateComment_ShouldThrowAccessForbiddenException_WhenNotCommentAuthor() {
        // Given
        User owner = createUser("Owner", "owner@email.com");
        User author = createUser("Author", "author@email.com");
        User otherUser = createUser("Other", "other@email.com");
        Item item = createItem(owner, "Name", "Description", true, null);

        Comment comment = createComment(author, item, "Comment");

        CommentCreateOrUpdateDto updateDto = new CommentCreateOrUpdateDto("New Comment");
        UpdateCommentCommand command = new UpdateCommentCommand(comment.getId(), item.getId(), otherUser.getId(), updateDto);

        AccessForbiddenException exception = assertThrows(AccessForbiddenException.class,
                () -> itemService.updateComment(command));

        assertTrue(exception.getMessage().contains("Forbidden to change comment not owned by user"));
    }

    @Test
    void deleteById_ShouldRemoveItem() {
        User owner = createUser("Owner", "owner@email.com");
        Item item = createItem(owner, "Name", "Description", true, null);

        assertNotNull(itemService.findById(item.getId(), owner.getId()));

        itemService.deleteById(item.getId());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.findById(item.getId(), owner.getId()));

        assertEquals("Item", exception.getEntityName());
        assertEquals(item.getId(), exception.getEntityId());
    }

    @Test
    void clear_ShouldRemoveAllItems() {
        User owner = createUser("Owner", "owner@email.com");
        createItem(owner, "Name1", "Description1", true, null);
        createItem(owner, "Name2", "Description2", true, null);

        assertEquals(2, itemService.findAll().size());

        itemService.clear();

        List<ItemResponseDto> items = itemService.findAll();
        assertNotNull(items);
        assertTrue(items.isEmpty());
    }

    @Test
    void deleteComment_ShouldRemoveComment() {
        User owner = createUser("Owner", "owner@email.com");
        User author = createUser("Author", "author@email.com");
        Item item = createItem(owner, "Name", "Description", true, null);

        Comment comment = createComment(author, item, "Comment");

        ItemResponseWithCommentsDto itemWithComment = itemService.findById(item.getId(), owner.getId());
        assertEquals(1, itemWithComment.comments().size());

        itemService.deleteComment(comment.getId());

        ItemResponseWithCommentsDto itemWithoutComment = itemService.findById(item.getId(), owner.getId());
        assertTrue(itemWithoutComment.comments().isEmpty());
    }
}