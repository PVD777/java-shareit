package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.utility.XHeaders;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping("{requestId}")
    public ItemRequestDtoOut getRequest(@RequestHeader(XHeaders.USER_ID_X_HEADER) int userId,
                                        @PathVariable int requestId) {
        return itemRequestService.get(requestId, userId);
    }

    @PostMapping
    public ItemRequestDtoOut createRequest(@RequestBody ItemRequestDtoIn itemRequestDtoIn,
                                           @RequestHeader(XHeaders.USER_ID_X_HEADER) int userId) {
        return itemRequestService.create(userId, itemRequestDtoIn);
    }

    @GetMapping
    public Collection<ItemRequestDtoOut> getOwnerRequests(@RequestHeader(XHeaders.USER_ID_X_HEADER) int userId) {
        return itemRequestService.getOwnerRequests(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDtoOut> getAllRequests(@RequestHeader(XHeaders.USER_ID_X_HEADER) int userId,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "999") int size) {
        return itemRequestService.getAll(userId, from, size);
    }

}
