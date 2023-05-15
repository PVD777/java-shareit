package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    ItemDto createItem(int userId, ItemDto itemDto);

    ItemDto getItem(int itemId);

    Collection<ItemDto> getAllItems();

    ItemDto updateItem(int userId, int itemId, ItemDto itemDto);

    void deleteItem(int id);

    List<ItemDto> getOwnersItem(int userId);

    List<ItemDto> getAvailableItems(String text);

}
