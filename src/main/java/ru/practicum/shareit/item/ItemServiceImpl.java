package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(int userid, ItemDto itemDto) {
        Item item = ItemMapper.dtoToItem(itemDto);
        item.setOwner(userRepository.getUser(userid).orElseThrow(() -> new ObjectNotFoundException("Пользовательно не найден")));
        return ItemMapper.toItemDto(itemRepository.createItem(item));
    }

    @Override
    public ItemDto getItem(int itemId) {
         return ItemMapper.toItemDto(itemRepository.getItem(itemId).orElseThrow(() ->
                 new  ObjectNotFoundException("запрошенная вещь не найдена")));

    }

    @Override
    public Collection<ItemDto> getAllItems() {
        return itemRepository.getAllItems().stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, ItemDto itemDto) {
        Item oldItem = itemRepository.getItem(itemId).get();
        Item patchedItem = ItemMapper.dtoToItem(itemDto);
        if (oldItem.getOwner().getId() != userId) {
            throw new ForbiddenException("Доступ запрещен");
        }
        if (patchedItem.getName() != null) {
            oldItem.setName(patchedItem.getName());
        }
        if (patchedItem.getDescription() != null) {
            oldItem.setDescription(patchedItem.getDescription());
        }
        if (patchedItem.getAvailable() != null) {
            oldItem.setAvailable(patchedItem.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.updateItem(itemId, oldItem));
    }

    @Override
    public void deleteItem(int id) {
        itemRepository.deleteItem(id);
    }

    @Override
    public List<ItemDto> getOwnersItem(int userId) {
        return itemRepository.getAllItems().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAvailableItems(String text) {
        if (text.isBlank()) return new ArrayList<>();
        return getAllItems().stream()
                .filter(itemDto -> itemDto.getAvailable())
                .filter(itemDto -> itemDto.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }
}
