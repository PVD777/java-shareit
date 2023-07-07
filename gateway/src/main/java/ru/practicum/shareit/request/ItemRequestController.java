package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private static final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody ItemRequestDto requestDto,
                                                @RequestHeader(name = X_SHARER_USER_ID_HEADER) int userId) {
        return itemRequestClient.createRequest(requestDto, userId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable int requestId,
                                             @RequestHeader(name = X_SHARER_USER_ID_HEADER) int userId) {
        return itemRequestClient.getRequest(requestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerRequests(@RequestHeader(name = X_SHARER_USER_ID_HEADER) int userId) {
        return itemRequestClient.getOwnerRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(name = X_SHARER_USER_ID_HEADER) int userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size
    ) {
        return itemRequestClient.getAllRequests(userId, from, size);
    }
}
