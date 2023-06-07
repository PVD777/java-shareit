package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(int userId, ItemDto itemDto) {
        Item item = ItemMapper.dtoToItem(itemDto);
        item.setOwner(validateUser(userId));
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(int itemId, int userId) {
        Item item = validateItem(itemId);
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
        Item oldItem = itemRepository.getReferenceById(itemId);
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
    public List<ItemDto> getOwnersItem(int userId) {
        return itemRepository.getItemsByOwnerId(userId).stream()
                .sorted(Comparator.comparingInt(Item::getId))
                .map(ItemMapper::toItemDto)
                .map(this::setBookingsToItem)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAvailableItems(String text) {
        if (text.isBlank()) return new ArrayList<>();
        return itemRepository.getItemByAvailableIsTrueAndDescriptionContainsIgnoreCase(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }


    public CommentDto addComment(int userId, int itemId, CommentDto commentDto) {
        Item item = validateItem(itemId);
        User user = validateUser(userId);
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

    private Item validateItem(int itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Указнный item не существует"));
    }

    private User validateUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрошенный пользователь не найден"));
    }
}


