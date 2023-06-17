package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.utility.ExistValidator;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
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
        User user = ExistValidator.validateUser(userRepository, userId);
        itemRequest.setUser(user);
        itemRequest.setCreatedDateTime(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto get(int requestId, int userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Пользователь не существует");
        }
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(ExistValidator
                .validateRequest(itemRequestRepository, requestId));
        return setItemsToRequestDto(itemRequestDto);
    }

    @Override
    public Collection<ItemRequestDto> getOwnerRequests(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Пользователь не существует");
        }
        return itemRequestRepository.getItemRequestsByUserIdOrderByCreatedDateTime(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .map(this::setItemsToRequestDto)
                .collect(Collectors.toList());

    }

    @Override
    public Collection<ItemRequestDto> getAll(int userId, int from, int size) {
        return itemRequestRepository.findAll(PageRequest.of(from, size)).stream()
                .filter(itemRequest -> itemRequest.getUser().getId() != userId)
                .sorted(Comparator.comparing(ItemRequest::getCreatedDateTime))
                .map(ItemRequestMapper::toItemRequestDto)
                .map(this::setItemsToRequestDto)
                .collect(Collectors.toList());
    }

    private ItemRequestDto setItemsToRequestDto(ItemRequestDto itemRequestDto) {
        itemRequestDto.setItems(itemRepository.getItemByRequestId(itemRequestDto.getId()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
        return itemRequestDto;
    }

}
