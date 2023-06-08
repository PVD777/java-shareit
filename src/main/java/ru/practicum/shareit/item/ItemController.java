package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utility.XHeaders;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(XHeaders.USER_ID_X_HEADER) int userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@NotBlank @RequestHeader(XHeaders.USER_ID_X_HEADER) int userId,
                              @PathVariable int itemId, @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("{itemId}")
    public ItemDto getItemById(@RequestHeader(XHeaders.USER_ID_X_HEADER) int userId, @PathVariable int itemId) {
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getOwnersItem(@RequestHeader(XHeaders.USER_ID_X_HEADER) int userId) {
        return itemService.getOwnersItem(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getAvailableItems(@RequestParam String text) {
        return itemService.getAvailableItems(text);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(@RequestHeader(XHeaders.USER_ID_X_HEADER) int userId,
                              @PathVariable int itemId, @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
