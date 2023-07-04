package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;


@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final RequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDtoOut create(int userId, ItemRequestDtoIn itemRequestDtoIn) {
        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(itemRequestDtoIn);
        User user = validateUser(userId);
        itemRequest.setUser(user);
        itemRequest.setCreatedDateTime(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDtoOut get(int requestId, int userId) {
        isUserExist(userId);
        ItemRequestDtoOut itemRequestDtoOut = ItemRequestMapper.toItemRequestDto(validateRequest(requestId));
        return setItemsToRequestDto(itemRequestDtoOut);
    }

    @Override
    public Collection<ItemRequestDtoOut> getOwnerRequests(int userId) {
        isUserExist(userId);
        Collection<ItemRequest> requests = itemRequestRepository.getItemRequestsByUserIdOrderByCreatedDateTime(userId);
        return getItemRequestDtos(requests);
    }

    @Override
    public Collection<ItemRequestDtoOut> getAll(int userId, int from, int size) {
        isUserExist(userId);
        Collection<ItemRequest> requests = itemRequestRepository
                .findItemRequestsByUserIdNotOrderByCreatedDateTime(userId, PageRequest.of(from / size, size));
        return getItemRequestDtos(requests);
    }

    private Collection<ItemRequestDtoOut> getItemRequestDtos(Collection<ItemRequest> requests) {
        List<Integer> requestIds = requests.stream().map(ItemRequest::getId).collect(toList());
        Map<Integer, List<Item>> itemsByRequestIds = getItemsByRequestIds(requestIds);
        return requests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .peek(itemRequestDto ->
                        itemRequestDto.setItems(itemsByRequestIds
                                .getOrDefault(itemRequestDto.getId(), Collections.emptyList()).stream()
                                .map(ItemMapper::toItemDto)
                                .collect(toList())))
                .collect(toList());
    }

    private ItemRequestDtoOut setItemsToRequestDto(ItemRequestDtoOut itemRequestDtoOut) {
        itemRequestDtoOut.setItems(itemRepository.getItemByRequestId(itemRequestDtoOut.getId()).stream()
                .map(ItemMapper::toItemDto)
                .collect(toList()));
        return itemRequestDtoOut;
    }

    private Map<Integer, List<Item>> getItemsByRequestIds(List<Integer> requestIds) {
        List<Item> items = itemRepository.findByRequestIdIn(requestIds);
        return items.stream()
                .collect(groupingBy(i -> i.getRequest().getId(), toList()));
    }

    private User validateUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрошенный User не найден"));
    }

    private ItemRequest validateRequest(int requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрошенный Request не найден"));
    }

    private void isUserExist(int userId) {
        if (!userRepository.existsById(userId))
            throw new ObjectNotFoundException("Пользователь не существует");
    }
}
