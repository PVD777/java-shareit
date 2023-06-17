package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dao.CommentRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.utility.ExistValidator;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RequestRepository itemRequestRepository;

    @Override
    public ItemDto createItem(int userId, ItemDto itemDto) {
        Item item = ItemMapper.dtoToItem(itemDto);
        item.setOwner(ExistValidator.validateUser(userRepository, userId));
        if (itemDto.getRequestId() != null) {
            ItemRequest request = ExistValidator.validateRequest(itemRequestRepository, itemDto.getRequestId());
            item.setRequest(request);
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(int itemId, int userId) {
        Item item = ExistValidator.validateItem(itemRepository, itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (item.getOwner().getId() == userId) {
            setBookingsToItem(itemDto);
        }
        itemDto.setComments(commentRepository.findCommentsByItemIdOrderByCreate(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        return itemDto;
    }

    @Override
    public Collection<ItemDto> getAllItems() {
        return itemRepository.findAll().stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, ItemDto itemDto) {
        Item oldItem = ExistValidator.validateItem(itemRepository, itemId);
        Item updatedItem = ItemMapper.dtoToItem(itemDto);
        if (oldItem.getOwner().getId() != userId) {
            throw new ForbiddenException("Доступ запрещен");
        }
        if (updatedItem.getName() != null) {
            oldItem.setName(updatedItem.getName());
        }
        if (updatedItem.getDescription() != null) {
            oldItem.setDescription(updatedItem.getDescription());
        }
        if (updatedItem.getAvailable() != null) {
            oldItem.setAvailable(updatedItem.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(oldItem));
    }

    @Override
    public void deleteItem(int id) {
        itemRepository.deleteById(id);

    }

    @Override
    public List<ItemDto> getOwnersItem(int userId, int from, int size) {
        return itemRepository.findItemsByOwnerIdOrderById(userId, PageRequest.of(from, size))
                .stream()
                .map(ItemMapper::toItemDto)
                .map(this::setBookingsToItem)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAvailableItems(String text, int from, int size) {
        if (text.isBlank()) return new ArrayList<>();
        return itemRepository.findItemsByAvailableIsTrueAndDescriptionContainsIgnoreCase(text,
                        PageRequest.of(from, size)).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }


    public CommentDto addComment(int userId, int itemId, CommentDto commentDto) {
        Item item = ExistValidator.validateItem(itemRepository, itemId);
        User user = ExistValidator.validateUser(userRepository, userId);
        boolean wasBooked = bookingRepository.findBookingsByItemIdOrderByBookingStart(itemId).stream()
                .filter(booking -> booking.getBookingFinish().isBefore(LocalDateTime.now()) &&
                        booking.getStatus().equals(BookingStatus.APPROVED))
                .anyMatch(booking -> booking.getUser().getId() == userId);

        if (!wasBooked) {
            throw new ValidationException("Доступ запрещен");
        }

        Comment comment = CommentMapper.dtoToComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreate(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private ItemDto setBookingsToItem(ItemDto item) {
        List<Booking> itemBookings = bookingRepository.findBookingsByItemIdOrderByBookingStart(item.getId());
        if (!itemBookings.isEmpty()) {
            Optional<Booking> lastBooking = itemBookings.stream()
                    .filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED) &&
                            booking.getBookingStart().isBefore(LocalDateTime.now()))
                    .reduce((first, second) -> second);
            lastBooking.ifPresent(booking -> item.setLastBooking(BookingMapper.bookingToDtoOutShort(booking)));

            Optional<Booking> nextBooking = itemBookings.stream()
                    .filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED) &&
                            booking.getBookingStart().isAfter(LocalDateTime.now()))
                    .findFirst();
            nextBooking.ifPresent(booking -> item.setNextBooking(BookingMapper.bookingToDtoOutShort(booking)));
        }
        return item;
    }
}


