package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dao.CommentRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestRepository requestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;


    List<User> users = new ArrayList<>();
    List<Item> items = new ArrayList<>();
    List<Comment> comments = new ArrayList<>();
    List<Booking> bookings = new ArrayList<>();
    List<ItemRequest> requests = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        users.clear();
        User user1 = new User();
        user1.setId(1);
        user1.setName("User 1");
        user1.setEmail("user1@yandex.ru");
        users.add(user1);
        User user2 = new User();
        user2.setId(2);
        user2.setName("User 2");
        user2.setEmail("user2@yandex.ru");
        users.add(user2);

        items.clear();
        Item item1 = new Item();
        item1.setId(1);
        item1.setName("Item 1");
        item1.setDescription("Item description 1");
        item1.setAvailable(true);
        item1.setOwner(user1);
        items.add(item1);
        Item item2 = new Item();
        item2.setId(2);
        item2.setName("Item 1");
        item2.setDescription("Item description 1");
        item2.setAvailable(true);
        item2.setOwner(user1);
        items.add(item2);


        comments.clear();
        Comment comment1 = new Comment();
        comment1.setId(1);
        comment1.setText("Comment1");
        comment1.setAuthor(user2);
        comment1.setCreate(LocalDateTime.now().minusMinutes(2));
        comment1.setItem(item1);
        comments.add(comment1);
        Comment comment2 = new Comment();
        comment2.setId(2);
        comment2.setText("Comment2");
        comment2.setAuthor(user2);
        comment2.setCreate(LocalDateTime.now().minusMinutes(1));
        comment2.setItem(item1);
        comments.add(comment2);

        bookings.clear();
        Booking booking1 = new Booking();
        booking1.setId(1);
        booking1.setItem(item1);
        booking1.setUser(user2);
        booking1.setStatus(BookingStatus.APPROVED);
        booking1.setBookingStart(LocalDateTime.now().minusDays(2));
        booking1.setBookingFinish(LocalDateTime.now().minusDays(1));
        bookings.add(booking1);
        Booking booking2 = new Booking();
        booking2.setId(2);
        booking2.setItem(item1);
        booking2.setUser(user2);
        booking2.setStatus(BookingStatus.WAITING);
        booking2.setBookingStart(LocalDateTime.now().plusDays(3));
        booking2.setBookingFinish(LocalDateTime.now().plusDays(4));
        bookings.add(booking2);

        requests.clear();
        ItemRequest itemRequest1 = new ItemRequest(1, "desc1", user1, LocalDateTime.now());
        ItemRequest itemRequest2 = new ItemRequest(2, "desc2", user1, LocalDateTime.now());
        requests.add(itemRequest1);
        requests.add(itemRequest2);
    }

    @Test
    void createItem() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(users.get(0)));

        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(items.get(0));

        Mockito
                .when(requestRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(requests.get(0)));
        items.get(0).setRequest(requests.get(0));
        ItemDto itemDto = ItemMapper.toItemDto(items.get(0));
        int ownerId = users.get(0).getId();
        ItemDto savedItemDto = itemService.createItem(ownerId, itemDto);


        assertNotNull(savedItemDto);
        assertEquals(itemDto.getId(), savedItemDto.getId());
        assertEquals(itemDto.getName(), savedItemDto.getName());
        assertEquals(itemDto.getDescription(), savedItemDto.getDescription());
    }

    @Test
    void getItem() {

        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(items.get(0)));
        Mockito
                .when(commentRepository.findCommentsByItemIdOrderByCreate(Mockito.anyInt()))
                .thenReturn(comments);
        Mockito
                .when(bookingRepository.findBookingsByItemIdOrderByBookingStart(Mockito.anyInt()))
                .thenReturn(bookings);

        ItemDto itemDto = itemService.getItem(Mockito.anyInt(), users.get(0).getId());

        ItemDto expectedIteDto = ItemMapper.toItemDto(items.get(0));
        assertNotNull(itemDto);
        assertEquals(expectedIteDto.getId(), itemDto.getId());
        assertEquals(bookings.get(1).getId(), itemDto.getNextBooking().getId());
        assertEquals(comments.size(), itemDto.getComments().size());
    }

    @Test
    void getAllItems() {
        Mockito
                .when(itemRepository.findAll())
                .thenReturn(items);

        Collection<ItemDto> itemsDto = itemService.getAllItems();
        assertEquals(items.size(), itemsDto.size());
    }

    @Test
    void updateItem() {
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(items.get(0)));

        User currentUser = users.get(0);
        Item updatedItem = new Item(1, "UpdatedItem1", "UpdatedDesc1", true);
        updatedItem.setOwner(currentUser);
        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(updatedItem);
        ItemDto updatedItemDto = ItemMapper.toItemDto(updatedItem);
        ItemDto savedItemDto = itemService.updateItem(currentUser.getId(), updatedItem.getId(), updatedItemDto);

        assertEquals(savedItemDto.getId(), updatedItemDto.getId());
        assertEquals(savedItemDto.getName(), updatedItemDto.getName());
        assertEquals(savedItemDto.getDescription(), updatedItemDto.getDescription());
        assertEquals(savedItemDto.getAvailable(), updatedItemDto.getAvailable());

    }

    @Test
    void getOwnersItem() {
        Mockito
                .when(itemRepository.findItemsByOwnerIdOrderById(Mockito.anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(items);
        Mockito
                .when(bookingRepository.findBookingsByItemIdOrderByBookingStart(Mockito.anyInt()))
                .thenReturn(bookings);
        User currentUser = users.get(0);
        List<ItemDto> ownerItems = itemService.getOwnersItem(currentUser.getId(), PageRequest.of(0,5));
        assertEquals(ownerItems.size(), items.size());
    }

    @Test
    void getAvailableItems() {
        Mockito
                .when(itemRepository.findItemsByAvailableIsTrueAndDescriptionContainsIgnoreCase(Mockito.anyString(),
                        Mockito.any(Pageable.class)))
                .thenReturn(items);
        List<ItemDto> searchedItems = itemService.getAvailableItems("test", PageRequest.of(0, 1));
        assertEquals(searchedItems.size(), 2);
        assertTrue(searchedItems.stream().allMatch(ItemDto::getAvailable));
    }

    @Test
    void addComment() {
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(items.get(0)));
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(users.get(1)));
        Mockito
                .when(bookingRepository.findBookingsByItemIdOrderByBookingStart(Mockito.anyInt()))
                .thenReturn(bookings);
        Mockito
                .when(commentRepository.save(Mockito.any(Comment.class)))
                .thenReturn(comments.get(0));

        CommentDto commentDto = CommentMapper.toCommentDto(comments.get(0));
        CommentDto savedComment = itemService.addComment(users.get(1).getId(), items.get(0).getId(), commentDto);
        assertEquals(savedComment.getId(), comments.get(0).getId());
        assertEquals(savedComment.getText(), comments.get(0).getText());
    }

    @Test
    void addCommentFail() {
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(items.get(0)));
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(users.get(1)));
        Mockito
                .when(bookingRepository.findBookingsByItemIdOrderByBookingStart(Mockito.anyInt()))
                .thenReturn(Collections.emptyList());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.addComment(users.get(1).getId(), items.get(0).getId(), new CommentDto()));
        assertEquals("Доступ запрещен", exception.getMessage());

    }
}