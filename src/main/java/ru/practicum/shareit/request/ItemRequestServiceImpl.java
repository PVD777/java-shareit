package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final RequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(int userId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(itemRequestDto);
        User user = validateUser(userId);
        itemRequest.setUser(user);
        itemRequest.setCreatedDateTime(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto get(int requestId, int userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Пользователь не существует");
        }
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(validateRequest(requestId));
        return setItemsToRequestDto(itemRequestDto);
    }

    @Override
    public Collection<ItemRequestDto> getOwnerRequests(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Пользователь не существует");
        }
        Collection<ItemRequest> requests = itemRequestRepository.getItemRequestsByUserIdOrderByCreatedDateTime(userId);
        List<Integer> requestIds = requests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        Map<Integer, List<Item>> itemsByRequestIds = getItemsByRequestIds(requestIds);
        return requests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .peek(itemRequestDto ->
                        itemRequestDto.setItems(itemsByRequestIds
                                .getOrDefault(itemRequestDto.getId(), Collections.emptyList()).stream()
                                .map(ItemMapper::toItemDto)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());

    }

    @Override
    public Collection<ItemRequestDto> getAll(int userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Пользователь не существует");
        }
        Collection<ItemRequest> requests = itemRequestRepository
                .findItemRequestsByUserIdNotOrderByCreatedDateTime(userId, PageRequest.of(from / size, size));
        List<Integer> requestIds = requests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        Map<Integer, List<Item>> itemsByRequestIds = getItemsByRequestIds(requestIds);
        return requests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .peek(itemRequestDto ->
                        itemRequestDto.setItems(itemsByRequestIds
                                .getOrDefault(itemRequestDto.getId(), Collections.emptyList()).stream()
                                .map(ItemMapper::toItemDto)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    private ItemRequestDto setItemsToRequestDto(ItemRequestDto itemRequestDto) {
        itemRequestDto.setItems(itemRepository.getItemByRequestId(itemRequestDto.getId()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
        return itemRequestDto;
    }

    private Map<Integer, List<Item>> getItemsByRequestIds(List<Integer> requestIds) {
        Map<Integer, List<Item>> itemsGroupByRequestIds = new HashMap<>();
        List<Item> items = itemRepository.findByRequestIdIn(requestIds);
        for (Item item : items) {
            Integer requestId = item.getRequest().getId();
            List<Item> itemsByRequestId = itemsGroupByRequestIds.get(requestId);
            if (itemsByRequestId == null) {
                itemsByRequestId = new ArrayList<>();
            }
            itemsByRequestId.add(item);
            itemsGroupByRequestIds.put(requestId, itemsByRequestId);
        }
        return itemsGroupByRequestIds;
    }

    private User validateUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрошенный User не найден"));
    }

    private ItemRequest validateRequest(int requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрошенный Request не найден"));
    }
}
