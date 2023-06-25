package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    List<User> users = new ArrayList<>();
    List<Item> items = new ArrayList<>();
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


        ItemRequest itemRequest1 = new ItemRequest("reqDesc1", user1, LocalDateTime.now().minusDays(1));
        ItemRequest itemRequest2 = new ItemRequest("reqDesc2", user1, LocalDateTime.now().minusHours(12));
        requests.add(itemRequest1);
        requests.add(itemRequest2);

        Item item3 = new Item("itemName3", "itemDesc3", true);
        item3.setOwner(user1);
        item3.setRequest(itemRequest1);
        Item item4 = new Item("itemName4", "itemDesc4", true);
        item4.setOwner(user1);
        item4.setRequest(itemRequest1);
        Item item5 = new Item("itemName5", "itemDesc5", true);
        item5.setOwner(user1);
        item5.setRequest(itemRequest2);
        items.add(itemRepository.save(item3));
        items.add(itemRepository.save(item4));
        items.add(itemRepository.save(item5));
    }

    @Test
    void create() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(users.get(0)));

        Mockito
                .when(requestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(requests.get(0));

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(requests.get(0));
        int ownerId = users.get(0).getId();
        ItemRequestDto savedRequest = itemRequestService.create(ownerId, itemRequestDto);
        assertEquals(requests.get(0).getId(), savedRequest.getId());
        assertEquals(requests.get(0).getDescription(), savedRequest.getDescription());
    }

    @Test
    void get() {
        Mockito
                .when(userRepository.existsById(Mockito.anyInt()))
                .thenReturn(true);
        Mockito
                .when(requestRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(requests.get(0)));
        int userId = users.get(0).getId();
        ItemRequestDto itemRequestDto = itemRequestService.get(requests.get(0).getId(), userId);
        assertEquals(itemRequestDto.getId(), requests.get(0).getId());
    }

    @Test
    void getOwnerRequests() {
        Mockito
                .when(userRepository.existsById(Mockito.anyInt()))
                .thenReturn(true);
        Mockito
                .when(requestRepository.getItemRequestsByUserIdOrderByCreatedDateTime(Mockito.anyInt()))
                .thenReturn(requests);
        int ownerId = users.get(0).getId();
        Collection<ItemRequestDto> requestDtos = itemRequestService.getOwnerRequests(ownerId);
        assertEquals(requestDtos.size(), 2);
    }

    @Test
    void getAll() {
        Page<ItemRequest> pagedRequest = new PageImpl<ItemRequest>(requests);
        Mockito
                .when(requestRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(pagedRequest);

        Collection<ItemRequestDto> allRequests = itemRequestService.getAll(2, 0, 99);
        assertEquals(allRequests.size(), 2);
    }
}