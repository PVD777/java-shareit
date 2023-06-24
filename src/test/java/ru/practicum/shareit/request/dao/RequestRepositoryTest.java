package ru.practicum.shareit.request.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;

    private final List<Item> items = new ArrayList<>();
    private final List<ItemRequest> requests = new ArrayList<>();
    private final List<User> users = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        User user1 = new User("user1", "user1@mail.com");
        User user2 = new User("user2", "user2@mail.com");
        User user3 = new User("user3", "user3@mail.com");
        users.add(userRepository.save(user1));
        users.add(userRepository.save(user2));
        users.add(userRepository.save(user3));

        Item item1 = new Item("itemName1", "itemDesc1", true);
        item1.setOwner(user1);
        Item item2 = new Item("itemName2", "itemDesc2", false);
        item2.setOwner(user2);
        Item item3 = new Item("itemName3", "itemDesc3", true);
        item3.setOwner(user3);
        Item item4 = new Item("itemName4", "itemDesc4", false);
        item4.setOwner(user2);
        Item item5 = new Item("itemName5", "itemDesc5", true);
        item5.setOwner(user1);
        Item item6 = new Item("itemName6", "itemDesc6", false);
        item6.setOwner(user2);
        items.add(itemRepository.save(item1));
        items.add(itemRepository.save(item2));
        items.add(itemRepository.save(item3));
        items.add(itemRepository.save(item4));
        items.add(itemRepository.save(item5));
        items.add(itemRepository.save(item6));

        ItemRequest itemRequest1 = new ItemRequest("reqDesc1", user1, LocalDateTime.now().minusDays(1));
        ItemRequest itemRequest2 = new ItemRequest("reqDesc2", user2, LocalDateTime.now().minusHours(12));
        ItemRequest itemRequest3 = new ItemRequest("reqDesc3", user2, LocalDateTime.now().minusMinutes(30));
        requests.add(requestRepository.save(itemRequest1));
        requests.add(requestRepository.save(itemRequest2));
        requests.add(requestRepository.save(itemRequest3));

        Item item7 = new Item("itemName7", "itemDesc7", true);
        item7.setOwner(user1);
        item7.setRequest(itemRequest1);
        Item item8 = new Item("itemName8", "itemDesc8", true);
        item8.setOwner(user1);
        item8.setRequest(itemRequest1);
        Item item9 = new Item("itemName9", "itemDesc9", true);
        item9.setOwner(user1);
        item9.setRequest(itemRequest2);
        items.add(itemRepository.save(item7));
        items.add(itemRepository.save(item8));
        items.add(itemRepository.save(item9));
    }

    @AfterEach
    void afterEach() {
        requests.clear();
        requestRepository.deleteAll();
        users.clear();
        userRepository.deleteAll();
        items.clear();
        itemRepository.deleteAll();
    }

    @Test
    void getItemRequestsByUserIdOrderByCreatedDateTime() {

        Collection<ItemRequest> requests1 = requestRepository
                .getItemRequestsByUserIdOrderByCreatedDateTime(users.get(0).getId());
        assertEquals(requests1.size(), 1);
        Collection<ItemRequest> requests2 = requestRepository
                .getItemRequestsByUserIdOrderByCreatedDateTime(users.get(1).getId());
        assertEquals(requests2.size(), 2);
        Collection<ItemRequest> requests3 = requestRepository
                .getItemRequestsByUserIdOrderByCreatedDateTime(users.get(2).getId());
        assertEquals(requests3.size(), 0);
    }

}