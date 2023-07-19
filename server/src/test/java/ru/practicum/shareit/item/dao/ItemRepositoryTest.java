package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private  UserRepository userRepository;
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
        ItemRequest itemRequest3 = new ItemRequest("reqDesc3", user3, LocalDateTime.now().minusMinutes(30));
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
    @DisplayName("Проверка поиска вещей по владельцу")
    void findItemsByOwnerIdOrderById() {
        List<Item> items1 = itemRepository.findItemsByOwnerIdOrderById(items.get(0).getOwner().getId(),
                Pageable.ofSize(20));
        assertEquals(items1.size(),5);
        assertTrue(items1.stream().allMatch(item -> item.getOwner().getId() == items.get(0).getOwner().getId()));

        List<Item> items2 = itemRepository.findItemsByOwnerIdOrderById(items.get(1).getOwner().getId(),
                Pageable.ofSize(20));

        assertEquals(items2.size(),3);
        assertTrue(items2.stream().allMatch(item -> item.getOwner().getId() == items.get(1).getOwner().getId()));

        items2 = itemRepository.findItemsByOwnerIdOrderById(items.get(1).getOwner().getId(),
                Pageable.ofSize(1));
        assertEquals(items2.size(),1);
        assertTrue(items2.stream().allMatch(item -> item.getOwner().getId() == items.get(1).getOwner().getId()));

        List<Item> items3 = itemRepository.findItemsByOwnerIdOrderById(items.get(2).getOwner().getId(),
                Pageable.ofSize(20));
        assertEquals(items3.size(),1);
        assertTrue(items3.stream().allMatch(item -> item.getOwner().getId() == items.get(2).getOwner().getId()));
    }

    @Test
    @DisplayName("Проверка поиска доступных вещей по описанию")
    void findItemsByAvailableIsTrueAndDescriptionContainsIgnoreCase() {
        List<Item> searchedItems = itemRepository.findItemsByNameOrDescription("dESC",
                Pageable.ofSize(2));
        assertEquals(searchedItems.size(),2);
        assertTrue(searchedItems.get(0).getDescription().contains("Desc") &&
                searchedItems.get(1).getDescription().contains("Desc"));
        assertTrue(searchedItems.get(0).getAvailable() & searchedItems.get(1).getAvailable());

        searchedItems = itemRepository.findItemsByNameOrDescription("DeSc",
                Pageable.ofSize(22));
        assertEquals(searchedItems.size(),6);
        assertTrue(searchedItems.stream().allMatch(Item::getAvailable));
        assertTrue(searchedItems.stream().allMatch(item -> item.getDescription().contains("Desc")));

    }

    @Test
    @DisplayName("Проверка поиска вещей по номеру request")
    void getItemByRequestId() {
        List<Item> items = itemRepository.getItemByRequestId(requests.get(0).getId());
        assertEquals(items.size(), 2);
        assertTrue(items.stream().allMatch(item -> item.getRequest().getId() == requests.get(0).getId()));
    }
}