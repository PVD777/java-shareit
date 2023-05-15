package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    private int itemIdCounter = 0;
    private Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        item.setId(++itemIdCounter);
        items.put(itemIdCounter,item);
        return item;
    }

    @Override
    public Optional<Item> getItem(int itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Collection<Item> getAllItems() {
        return items.values();
    }

    @Override
    public Item updateItem(int id, Item item) {
        items.put(id, item);
        return item;
    }

    @Override
    public void deleteItem(int id) {
        items.remove(id);
    }
}
