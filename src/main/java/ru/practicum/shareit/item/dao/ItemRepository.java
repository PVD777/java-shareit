package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Item createItem(Item item);

    Optional<Item> getItem(int itemId);

    Collection<Item> getAllItems();

    Item updateItem(int id, Item item);

    void deleteItem(int id);
}
