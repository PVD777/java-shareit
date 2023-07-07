package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDto;
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
    public ItemDto createItem(@RequestHeader(XHeaders.USER_ID_X_HEADER) int userId, @RequestBody ItemDto itemDto) {
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
    public List<ItemDto> getOwnersItem(@RequestHeader(XHeaders.USER_ID_X_HEADER) int userId,
                                       @RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "999") int size) {
        return itemService.getOwnersItem(userId, PageRequest.of(from, size));
    }

    @GetMapping("/search")
    public List<ItemDto> getAvailableItems(@RequestParam String text,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "999") int size) {
        return itemService.getAvailableItems(text, PageRequest.of(from, size));
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(@RequestHeader(XHeaders.USER_ID_X_HEADER) int userId,
                              @PathVariable int itemId, @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
