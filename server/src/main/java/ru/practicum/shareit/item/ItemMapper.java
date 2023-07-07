package ru.practicum.shareit.item;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto =  new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
        itemDto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        return itemDto;
    }

    public static Item dtoToItem(ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
    }
}