package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.comment.CommentDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    ItemDto createItem(int userId, ItemDto itemDto);

    ItemDto getItem(int itemId, int userId);

    Collection<ItemDto> getAllItems();

    ItemDto updateItem(int userId, int itemId, ItemDto itemDto);

    void deleteItem(int id);

    List<ItemDto> getOwnersItem(int userId, Pageable pageable);

    List<ItemDto> getAvailableItems(String text, Pageable pageable);

    CommentDto addComment(int userId, int itemId, CommentDto commentDto);

}
