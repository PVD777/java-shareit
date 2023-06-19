package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RequestRepository requestRepository;



    @BeforeEach
    void beforeEach() {
        User user1 = new User("user1", "user1@mail.com");
        user1.setId(1);
        User user2 = new User("user2", "user2@mail.com");
        user2.setId(2);
        User user3 = new User("user3", "user3@mail.com");
        user3.setId(3);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        Item item1 = new Item(1, "itemName1", "itemDesc1", true);
        item1.setOwner(user1);
        Item item2 = new Item(2, "itemName2", "itemDesc2", false);
        item2.setOwner(user2);
        Item item3 = new Item(3, "itemName3", "itemDesc3", true);
        item3.setOwner(user3);
        Item item4 = new Item(4, "itemName4", "itemDesc4", false);
        item4.setOwner(user2);
        Item item5 = new Item(5, "itemName5", "itemDesc5", true);
        item5.setOwner(user1);
        Item item6 = new Item(6, "itemName6", "itemDesc6", false);
        item6.setOwner(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
        itemRepository.save(item4);
        itemRepository.save(item5);
        itemRepository.save(item6);

        ItemRequest itemRequest1 = new ItemRequest(1, "reqDesc1", user1, LocalDateTime.now().minusDays(1));
        ItemRequest itemRequest2 = new ItemRequest(2, "reqDesc2", user2, LocalDateTime.now().minusHours(12));
        ItemRequest itemRequest3 = new ItemRequest(3, "reqDesc3", user3, LocalDateTime.now().minusMinutes(30));
        requestRepository.save(itemRequest1);
        requestRepository.save(itemRequest2);
        requestRepository.save(itemRequest3);

        Item item7 = new Item(7, "itemName7", "itemDesc7", true);
        item7.setOwner(user1);
        item7.setRequest(itemRequest1);
        Item item8 = new Item(8, "itemName8", "itemDesc8", true);
        item8.setOwner(user1);
        item8.setRequest(itemRequest1);
        Item item9 = new Item(9, "itemName9", "itemDesc9", true);
        item9.setOwner(user1);
        item9.setRequest(itemRequest2);
        itemRepository.save(item7);
        itemRepository.save(item8);
        itemRepository.save(item9);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        requestRepository.deleteAll();
    }
    @Test
    @DisplayName("Проверка поиска вещей по владельцу")
    void findItemsByOwnerIdOrderById() {
        List<Item> items1 = itemRepository.findItemsByOwnerIdOrderById(1, Pageable.ofSize(20));
        assertEquals(items1.size(),5);
        assertEquals(items1.get(0).getOwner().getId(), 1);
        assertEquals(items1.get(1).getId(), 5);

        List<Item> items2 = itemRepository.findItemsByOwnerIdOrderById(2, Pageable.ofSize(20));
        assertEquals(items2.size(),3);
        assertEquals(items2.get(1).getOwner().getId(), 2);
        assertEquals(items2.get(2).getId(), 6);

        items2 = itemRepository.findItemsByOwnerIdOrderById(2, Pageable.ofSize(1));
        assertEquals(items2.size(),1);
        assertEquals(items2.get(0).getOwner().getId(), 2);
        assertEquals(items2.get(0).getId(), 2);

        List<Item> items3 = itemRepository.findItemsByOwnerIdOrderById(3, Pageable.ofSize(20));
        assertEquals(items3.size(),1);
        assertEquals(items3.get(0).getOwner().getId(), 3);
        assertEquals(items3.get(0).getId(), 3);
    }

    @Test
    @DisplayName("Проверка поиска доступных вещей по описанию")
    void findItemsByAvailableIsTrueAndDescriptionContainsIgnoreCase() {
        List<Item> items = itemRepository.findItemsByAvailableIsTrueAndDescriptionContainsIgnoreCase("dESC",
                Pageable.ofSize(2));
        assertEquals(items.size(),2);
        assertTrue(items.get(0).getDescription().contains("Desc") &&
                items.get(1).getDescription().contains("Desc"));
        assertTrue(items.get(0).getAvailable() & items.get(1).getAvailable());

        items = itemRepository.findItemsByAvailableIsTrueAndDescriptionContainsIgnoreCase("DeSc",
                Pageable.ofSize(22));
        assertEquals(items.size(),6);
        assertTrue(items.stream().allMatch(Item::getAvailable));
        assertTrue(items.stream().allMatch(item -> item.getDescription().contains("Desc")));

    }

    @Test
    void getItemByRequestId() {
        List<Item> items = itemRepository.getItemByRequestId(1);
        assertEquals(items.size(), 2);
        assertTrue(items.stream().allMatch(item -> item.getRequest().getId() == 1));
    }
}